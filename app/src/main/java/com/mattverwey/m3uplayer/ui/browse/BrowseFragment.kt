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
        title = "Home"
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_placeholder)
        
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
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, row ->
            if (item is Channel) {
                // For live TV, play directly. For VOD, show details first
                if (item.category == ChannelCategory.LIVE_TV) {
                    playChannel(item)
                } else {
                    showDetails(item)
                }
            }
        }
        
        // Handle header (sidebar menu) selection - open Settings when Settings row is selected
        setOnItemViewSelectedListener { _, item, _, row ->
            if (row is ListRow) {
                val headerTitle = row.headerItem?.name ?: ""
                // Check if Settings row is selected and no content items exist
                if (headerTitle.contains("Settings") && item == null) {
                    // Show options menu when Settings row is focused
                    showOptionsMenu()
                }
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
        
        // HOME PAGE SECTIONS - Create a proper landing page
        
        // 1. Recently Watched - Show at the top for easy access
        val recentlyWatched = repository.getRecentlyWatchedChannels(channels)
        if (recentlyWatched.isNotEmpty()) {
            addRow("⏱️ Recently Watched", recentlyWatched.take(20))
        }
        
        // 2. Recommendations - Based on watch history
        val recommendations = repository.getRecommendations(channels, maxRecommendations = 20)
        if (recommendations.isNotEmpty()) {
            addRow("💡 Recommended For You", recommendations)
        }
        
        // 3. Latest Added - Recently added content
        val latestAdded = repository.getLatestAddedContent(channels, limit = 20)
        if (latestAdded.isNotEmpty()) {
            addRow("🆕 Latest Added", latestAdded)
        }
        
        // CATEGORY SECTIONS - Full browsable categories
        
        // 4. Live TV - All live channels
        if (liveChannels.isNotEmpty()) {
            addRow("📺 Live TV", liveChannels.take(30))
        }
        
        // 5. Movies - All movies
        if (movies.isNotEmpty()) {
            addRow("🎬 Movies", movies.take(30))
        }
        
        // 6. Series - All series
        if (series.isNotEmpty()) {
            addRow("📺 Series", series.take(30))
        }
        
        // 7. Settings - Empty row for settings menu
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
        repository.addToRecentlyWatched(channel.id)
        
        val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
            putExtra(PlaybackActivity.EXTRA_CHANNEL, channel)
        }
        startActivity(intent)
    }
    
    private fun showDetails(channel: Channel) {
        val intent = Intent(requireContext(), DetailsActivity::class.java).apply {
            putExtra(DetailsActivity.EXTRA_CHANNEL, channel)
        }
        startActivity(intent)
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
        
        // Refresh rows to update Recently Watched when returning from playback
        if (allChannels.isNotEmpty()) {
            setupRows(allChannels)
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
