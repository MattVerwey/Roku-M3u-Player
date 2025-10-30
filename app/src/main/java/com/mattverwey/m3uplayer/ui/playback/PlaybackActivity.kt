package com.mattverwey.m3uplayer.ui.playback

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.ChannelCategory
import com.mattverwey.m3uplayer.data.model.EPGProgram
import com.mattverwey.m3uplayer.databinding.ActivityPlaybackBinding
import com.mattverwey.m3uplayer.network.EPGService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PlaybackActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlaybackBinding
    private var player: ExoPlayer? = null
    private var channel: Channel? = null
    private var isInPipMode = false
    private lateinit var trackSelectionDialog: TrackSelectionDialog
    private lateinit var epgService: EPGService
    private var epgData: Map<String, List<EPGProgram>> = emptyMap()
    private var controlsVisible = false
    private val handler = Handler(Looper.getMainLooper())
    private var hideControlsRunnable: Runnable? = null
    private var seriesPlaybackHelper: SeriesPlaybackHelper? = null
    
    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
        const val EXTRA_SERIES_INFO = "extra_series_info"
        const val EXTRA_CREDENTIALS = "extra_credentials"
        private const val SEEK_INCREMENT_MS = 10000L // 10 seconds
        private const val CONTROLS_HIDE_DELAY_MS = 5000L // 5 seconds
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaybackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide system UI for immersive experience
        hideSystemUI()
        
        channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CHANNEL, Channel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CHANNEL)
        }
        
        if (channel == null) {
            Toast.makeText(this, "Error: No channel provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        epgService = EPGService()
        
        // Initialize series helper if this is a series episode
        if (channel?.category == ChannelCategory.SERIES) {
            val seriesInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_SERIES_INFO, com.mattverwey.m3uplayer.data.model.XtreamSeriesInfo::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_SERIES_INFO)
            }
            
            val credentials = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(EXTRA_CREDENTIALS, com.mattverwey.m3uplayer.data.model.XtreamCredentials::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(EXTRA_CREDENTIALS)
            }
            
            if (seriesInfo != null && credentials != null) {
                seriesPlaybackHelper = SeriesPlaybackHelper(seriesInfo, credentials)
            }
        }
        
        setupPlayer()
        setupUI()
        setupControls()
        loadEPGData()
    }
    
    private fun setupPlayer() {
        // Configure custom load control for better buffering
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                60000, // Max buffer 60 seconds (increased from default 50s)
                2500,  // Buffer for playback 2.5 seconds (increased from default 2.5s)
                5000   // Buffer for playback after rebuffer 5 seconds (increased from default 5s)
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        
        // Configure track selector for adaptive streaming
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(
                buildUponParameters()
                    .setMaxVideoSizeSd() // Start with SD quality for faster startup
                    .setForceHighestSupportedBitrate(false) // Allow adaptive bitrate
            )
        }
        
        player = ExoPlayer.Builder(this)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            
            // Initialize track selection dialog
            trackSelectionDialog = TrackSelectionDialog(this, exoPlayer)
            
            // Set up media item
            val mediaItem = MediaItem.fromUri(channel!!.streamUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            
            // Add listener for playback events
            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Toast.makeText(
                        this@PlaybackActivity,
                        "Playback error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        Player.STATE_READY -> {
                            binding.progressBar.visibility = View.GONE
                        }
                        Player.STATE_ENDED -> {
                            handlePlaybackEnded()
                        }
                    }
                }
            })
        }
    }
    
    private fun setupUI() {
        binding.channelName.text = channel?.name
        
        // Hide channel name after 3 seconds
        binding.channelName.postDelayed({
            binding.channelName.visibility = View.GONE
        }, 3000)
        
        // Initially hide controls
        hideControls()
    }
    
    private fun setupControls() {
        // Get control views
        val playerControls = findViewById<LinearLayout>(R.id.player_controls)
        val btnSubtitles = findViewById<Button>(R.id.btn_subtitles)
        val btnAudioTrack = findViewById<Button>(R.id.btn_audio_track)
        val btnVideoQuality = findViewById<Button>(R.id.btn_video_quality)
        val btnRewind = findViewById<Button>(R.id.btn_rewind)
        val btnPlayPause = findViewById<Button>(R.id.exo_play_pause)
        val btnFastForward = findViewById<Button>(R.id.btn_fast_forward)
        val btnTvGuide = findViewById<Button>(R.id.btn_tv_guide)
        val btnPlayNext = findViewById<Button>(R.id.btn_play_next)
        
        // Setup subtitle button
        btnSubtitles.setOnClickListener {
            trackSelectionDialog.showTrackSelectionDialog(TrackSelectionDialog.TrackType.SUBTITLE)
        }
        
        // Setup audio track button
        btnAudioTrack.setOnClickListener {
            trackSelectionDialog.showTrackSelectionDialog(TrackSelectionDialog.TrackType.AUDIO)
        }
        
        // Setup video quality button
        btnVideoQuality.setOnClickListener {
            trackSelectionDialog.showTrackSelectionDialog(TrackSelectionDialog.TrackType.VIDEO)
        }
        
        // Setup rewind button
        btnRewind.setOnClickListener {
            player?.let { p ->
                val newPosition = (p.currentPosition - SEEK_INCREMENT_MS).coerceAtLeast(0)
                p.seekTo(newPosition)
            }
            resetHideControlsTimer()
        }
        
        // Setup play/pause button
        btnPlayPause.setOnClickListener {
            player?.let { p ->
                if (p.isPlaying) {
                    p.pause()
                    btnPlayPause.text = getString(R.string.play)
                } else {
                    p.play()
                    btnPlayPause.text = getString(R.string.pause)
                }
            }
            resetHideControlsTimer()
        }
        
        // Setup fast forward button
        btnFastForward.setOnClickListener {
            player?.let { p ->
                val duration = p.duration
                val newPosition = if (duration != C.TIME_UNSET) {
                    (p.currentPosition + SEEK_INCREMENT_MS).coerceAtMost(duration)
                } else {
                    p.currentPosition + SEEK_INCREMENT_MS
                }
                p.seekTo(newPosition)
            }
            resetHideControlsTimer()
        }
        
        // Setup TV guide button (only for live TV)
        if (channel?.category == ChannelCategory.LIVE_TV) {
            btnTvGuide.visibility = View.VISIBLE
            btnTvGuide.setOnClickListener {
                toggleTVGuide()
                resetHideControlsTimer()
            }
        } else {
            btnTvGuide.visibility = View.GONE
        }
        
        // Setup play next button (only for series)
        if (channel?.category == ChannelCategory.SERIES && seriesPlaybackHelper != null) {
            val hasNext = channel?.seasonNumber?.let { season ->
                channel?.episodeNumber?.let { episode ->
                    seriesPlaybackHelper?.hasNextEpisode(season, episode) ?: false
                }
            } ?: false
            
            if (hasNext) {
                btnPlayNext.visibility = View.VISIBLE
                btnPlayNext.setOnClickListener {
                    playNextEpisode()
                }
            } else {
                btnPlayNext.visibility = View.GONE
            }
        } else {
            btnPlayNext.visibility = View.GONE
        }
        
        // Request focus on play/pause button
        btnPlayPause.requestFocus()
    }
    
    private fun showControls() {
        val playerControls = findViewById<LinearLayout>(R.id.player_controls)
        playerControls.visibility = View.VISIBLE
        controlsVisible = true
        resetHideControlsTimer()
    }
    
    private fun hideControls() {
        val playerControls = findViewById<LinearLayout>(R.id.player_controls)
        playerControls.visibility = View.GONE
        val tvGuideLayout = findViewById<LinearLayout>(R.id.tv_guide_layout)
        tvGuideLayout.visibility = View.GONE
        controlsVisible = false
        hideControlsRunnable?.let { handler.removeCallbacks(it) }
    }
    
    private fun toggleControls() {
        if (controlsVisible) {
            hideControls()
        } else {
            showControls()
        }
    }
    
    private fun resetHideControlsTimer() {
        hideControlsRunnable?.let { handler.removeCallbacks(it) }
        hideControlsRunnable = Runnable { hideControls() }
        handler.postDelayed(hideControlsRunnable!!, CONTROLS_HIDE_DELAY_MS)
    }
    
    private fun loadEPGData() {
        if (channel?.category != ChannelCategory.LIVE_TV) return
        
        lifecycleScope.launch {
            val result = epgService.downloadEPG()
            result.onSuccess { data ->
                epgData = data
                updateTVGuide()
            }.onFailure {
                // EPG loading failed, but playback continues
            }
        }
    }
    
    private fun toggleTVGuide() {
        val tvGuideLayout = findViewById<LinearLayout>(R.id.tv_guide_layout)
        if (tvGuideLayout.visibility == View.VISIBLE) {
            tvGuideLayout.visibility = View.GONE
        } else {
            updateTVGuide()
            tvGuideLayout.visibility = View.VISIBLE
        }
    }
    
    private fun updateTVGuide() {
        val tvGuideLayout = findViewById<LinearLayout>(R.id.tv_guide_layout)
        val currentProgramContainer = tvGuideLayout.findViewById<LinearLayout>(R.id.current_program_container)
        val upcomingProgramContainer = tvGuideLayout.findViewById<LinearLayout>(R.id.upcoming_program_container)
        val noEpgMessage = tvGuideLayout.findViewById<TextView>(R.id.no_epg_message)
        
        val (current, upcoming) = epgService.getCurrentAndUpcomingPrograms(
            channel?.epgChannelId,
            epgData
        )
        
        if (current == null && upcoming == null) {
            currentProgramContainer.visibility = View.GONE
            upcomingProgramContainer.visibility = View.GONE
            noEpgMessage.visibility = View.VISIBLE
        } else {
            noEpgMessage.visibility = View.GONE
            
            if (current != null) {
                currentProgramContainer.visibility = View.VISIBLE
                updateProgramInfo(currentProgramContainer, current)
            } else {
                currentProgramContainer.visibility = View.GONE
            }
            
            if (upcoming != null) {
                upcomingProgramContainer.visibility = View.VISIBLE
                updateProgramInfo(upcomingProgramContainer, upcoming)
            } else {
                upcomingProgramContainer.visibility = View.GONE
            }
        }
    }
    
    private fun updateProgramInfo(container: LinearLayout, program: EPGProgram) {
        val titleView = container.findViewById<TextView>(
            if (container.id == R.id.current_program_container) 
                R.id.current_program_title 
            else 
                R.id.upcoming_program_title
        )
        val timeView = container.findViewById<TextView>(
            if (container.id == R.id.current_program_container) 
                R.id.current_program_time 
            else 
                R.id.upcoming_program_time
        )
        val descView = container.findViewById<TextView>(
            if (container.id == R.id.current_program_container) 
                R.id.current_program_description 
            else 
                R.id.upcoming_program_description
        )
        
        titleView.text = program.title
        timeView.text = formatProgramTime(program)
        descView.text = program.description ?: ""
    }
    
    private fun formatProgramTime(program: EPGProgram): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val startTime = timeFormat.format(Date(program.startTime))
        val endTime = timeFormat.format(Date(program.endTime))
        return "$startTime - $endTime"
    }
    
    private fun playNextEpisode() {
        if (seriesPlaybackHelper == null || channel?.seasonNumber == null || channel?.episodeNumber == null) {
            finish()
            return
        }
        
        val nextEpisode = seriesPlaybackHelper?.getNextEpisode(
            channel!!.seasonNumber!!,
            channel!!.episodeNumber!!
        )
        
        if (nextEpisode != null) {
            val nextUrl = seriesPlaybackHelper?.getEpisodeStreamUrl(nextEpisode)
            if (nextUrl != null) {
                player?.let { p ->
                    val mediaItem = MediaItem.fromUri(nextUrl)
                    p.setMediaItem(mediaItem)
                    p.prepare()
                    p.playWhenReady = true
                    
                    // Update channel info
                    channel = channel?.copy(
                        name = seriesPlaybackHelper?.getEpisodeTitle(nextEpisode) ?: nextEpisode.title,
                        streamUrl = nextUrl,
                        seasonNumber = nextEpisode.season,
                        episodeNumber = nextEpisode.episode_num
                    )
                    
                    // Update UI
                    binding.channelName.text = channel?.name
                    binding.channelName.visibility = View.VISIBLE
                    binding.channelName.postDelayed({
                        binding.channelName.visibility = View.GONE
                    }, 3000)
                }
            }
        } else {
            finish()
        }
    }
    
    private fun handlePlaybackEnded() {
        if (channel?.category == ChannelCategory.SERIES && seriesPlaybackHelper != null) {
            // Auto-play next episode
            playNextEpisode()
        } else {
            finish()
        }
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player?.let {
                    if (it.isPlaying) it.pause() else it.play()
                }
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                if (!controlsVisible) {
                    toggleControls()
                    true
                } else {
                    false
                }
            }
            KeyEvent.KEYCODE_BACK -> {
                if (controlsVisible) {
                    hideControls()
                    true
                } else {
                    super.onKeyDown(keyCode, event)
                }
            }
            KeyEvent.KEYCODE_MENU -> {
                toggleControls()
                true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                player?.let { p ->
                    val newPosition = (p.currentPosition - SEEK_INCREMENT_MS).coerceAtLeast(0)
                    p.seekTo(newPosition)
                }
                true
            }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                player?.let { p ->
                    val newPosition = (p.currentPosition + SEEK_INCREMENT_MS).coerceAtMost(p.duration)
                    p.seekTo(newPosition)
                }
                true
            }
            KeyEvent.KEYCODE_WINDOW -> {
                // Enter PIP mode on Fire TV remote button
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterPictureInPictureMode()
                }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPictureInPictureMode() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }
    
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipMode = isInPictureInPictureMode
        
        if (isInPictureInPictureMode) {
            // Hide UI controls in PIP mode
            binding.channelName.visibility = View.GONE
            binding.playerView.hideController()
        } else {
            // Show UI controls when exiting PIP
            binding.playerView.showController()
        }
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Automatically enter PIP when user presses home
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && player?.isPlaying == true) {
            enterPictureInPictureMode()
        }
    }
    
    override fun onStop() {
        super.onStop()
        if (!isInPipMode) {
            releasePlayer()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        hideControlsRunnable?.let { handler.removeCallbacks(it) }
        releasePlayer()
    }
    
    private fun releasePlayer() {
        player?.release()
        player = null
    }
    
    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
        )
    }
}
