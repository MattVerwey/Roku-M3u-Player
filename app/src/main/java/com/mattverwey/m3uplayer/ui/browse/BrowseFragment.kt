package com.mattverwey.m3uplayer.ui.browse

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.data.model.ChannelCategory
import com.mattverwey.m3uplayer.repository.ChannelRepository
import com.mattverwey.m3uplayer.ui.details.DetailsActivity
import com.mattverwey.m3uplayer.ui.playback.PlaybackActivity
import com.mattverwey.m3uplayer.ui.settings.SettingsActivity
import kotlinx.coroutines.launch

class BrowseFragment : BrowseSupportFragment() {
    
    private lateinit var repository: ChannelRepository
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private var allChannels = listOf<Channel>()
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        setupUI()
        repository = ChannelRepository(CacheManager(requireContext()))
        loadChannels()
        setupEventListeners()
    }
    
    private fun setupUI() {
        title = "M3U Player"
        
        // Enable headers (left sidebar) - START WITH IT VISIBLE
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        
        // Set colors - dark sidebar with white text
        brandColor = android.graphics.Color.parseColor("#0D0D0D")
        searchAffordanceColor = android.graphics.Color.WHITE
        
        // Disable search
        setOnSearchClickedListener {
            Toast.makeText(requireContext(), "Search not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupEventListeners() {
        // Handle item clicks
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            try {
                if (item is Channel) {
                    // For live TV, play directly. For VOD, show details first
                    if (item.category == ChannelCategory.LIVE_TV) {
                        playChannel(item)
                    } else {
                        showDetails(item)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        // Handle header (sidebar menu) selection
        setOnItemViewSelectedListener { _, item, _, row ->
            if (row is ListRow) {
                val headerTitle = row.headerItem?.name ?: ""
                // Just highlight the selected menu item
                // Actual navigation happens when user presses RIGHT
            }
        }
    }
    
    private fun loadChannels() {
        showProgressBar()
        
        lifecycleScope.launch {
            val result = repository.loadChannels()
            
            result.onSuccess { channels ->
                allChannels = channels
                setupRows(channels)
                hideProgressBar()
            }.onFailure { error ->
                hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun setupRows(channels: List<Channel>) {
        val listRowPresenter = ListRowPresenter()
        rowsAdapter = ArrayObjectAdapter(listRowPresenter)
        
        // Store channels for later use
        allChannels = channels
        
        // Separate channels by category
        val liveChannels = channels.filter { it.category == ChannelCategory.LIVE_TV }
        val movies = channels.filter { it.category == ChannelCategory.MOVIE }
        val series = channels.filter { it.category == ChannelCategory.SERIES }
        
        // Create MAIN MENU with only 4 options
        // Each main menu item will show a placeholder row
        
        // 1. Live TV
        if (liveChannels.isNotEmpty()) {
            val sampleLive = liveChannels.take(10)
            addRow("📺 Live TV", sampleLive)
        }
        
        // 2. Movies
        if (movies.isNotEmpty()) {
            val sampleMovies = movies.take(10)
            addRow("🎬 Movies", sampleMovies)
        }
        
        // 3. Series
        if (series.isNotEmpty()) {
            val sampleSeries = series.take(10)
            addRow("📺 Series", sampleSeries)
        }
        
        // 4. Settings (empty row for now)
        val emptyRow = ArrayObjectAdapter(ChannelCardPresenter())
        val settingsHeader = HeaderItem(rowsAdapter.size().toLong(), "⚙️ Settings")
        rowsAdapter.add(ListRow(settingsHeader, emptyRow))
        
        adapter = rowsAdapter
        
        // Start with headers visible and focused
        selectedPosition = 0
    }
    
    private fun addRow(title: String, channels: List<Channel>) {
        val cardPresenter = ChannelCardPresenter()
        val listRowAdapter = ArrayObjectAdapter(cardPresenter)
        
        channels.forEach { channel ->
            listRowAdapter.add(channel)
        }
        
        val header = HeaderItem(rowsAdapter.size().toLong(), title)
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }
    
    private fun playChannel(channel: Channel) {
        // Validate stream URL before attempting playback
        if (channel.streamUrl.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Error: Invalid stream URL for this channel",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        // Series content should not be played directly, show details instead
        if (channel.category == ChannelCategory.SERIES) {
            showDetails(channel)
            return
        }
        
        repository.addToRecentlyWatched(channel.id)
        
        try {
            val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
                putExtra(PlaybackActivity.EXTRA_CHANNEL, channel)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error starting playback: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun showDetails(channel: Channel) {
        try {
            val intent = Intent(requireContext(), DetailsActivity::class.java).apply {
                putExtra(DetailsActivity.EXTRA_CHANNEL, channel)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error opening details: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun showProgressBar() {
        progressBarManager?.show()
    }
    
    private fun hideProgressBar() {
        progressBarManager?.hide()
    }
    
    override fun onResume() {
        super.onResume()
        
        // Always start with sidebar visible
        if (!isShowingHeaders) {
            startHeadersTransition(true)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        view?.setOnKeyListener(null)
    }
    
    private fun showOptionsMenu() {
        val options = arrayOf(
            "Refresh Channels",
            "Privacy & Security Settings"
        )
        
        AlertDialog.Builder(requireContext())
            .setTitle("Options")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> refreshChannels()
                    1 -> openSettings()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun refreshChannels() {
        Toast.makeText(requireContext(), "Refreshing channels...", Toast.LENGTH_SHORT).show()
        repository.clearCache()
        loadChannels()
    }
    
    private fun openSettings() {
        SettingsActivity.start(requireContext())
    }
}
