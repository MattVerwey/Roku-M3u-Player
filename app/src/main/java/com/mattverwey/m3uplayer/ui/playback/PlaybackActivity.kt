package com.mattverwey.m3uplayer.ui.playback

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.databinding.ActivityPlaybackBinding

class PlaybackActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlaybackBinding
    private var player: ExoPlayer? = null
    private var channel: Channel? = null
    private var isInPipMode = false
    
    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
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
        
        setupPlayer()
        setupUI()
    }
    
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            
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
                            finish()
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
    }
    
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                player?.let {
                    if (it.isPlaying) it.pause() else it.play()
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
