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
import com.mattverwey.m3uplayer.ui.login.LoginActivity
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
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = ContextCompat.getColor(requireContext(), R.color.primary)
    }
    
    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Channel) {
                // For live TV, play directly. For VOD, show details first
                if (item.category == ChannelCategory.LIVE_TV) {
                    playChannel(item)
                } else {
                    showDetails(item)
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
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        
        // Recently Watched
        val recentChannels = repository.getRecentlyWatchedChannels(channels)
        if (recentChannels.isNotEmpty()) {
            addRow(getString(R.string.recently_watched), recentChannels)
        }
        
        // Live TV
        val liveChannels = channels.filter { it.category == ChannelCategory.LIVE_TV }
        if (liveChannels.isNotEmpty()) {
            addRow(getString(R.string.live_tv), liveChannels)
        }
        
        // Movies
        val movies = channels.filter { it.category == ChannelCategory.MOVIE }
        if (movies.isNotEmpty()) {
            addRow(getString(R.string.movies), movies)
        }
        
        // Series
        val series = channels.filter { it.category == ChannelCategory.SERIES }
        if (series.isNotEmpty()) {
            addRow(getString(R.string.series), series)
        }
        
        // Group by category for Live TV
        if (liveChannels.isNotEmpty()) {
            val grouped = liveChannels.groupBy { it.groupTitle ?: "Other" }
            grouped.forEach { (categoryName, categoryChannels) ->
                if (categoryName != "Other" && categoryChannels.size > 3) {
                    addRow(categoryName, categoryChannels)
                }
            }
        }
        
        adapter = rowsAdapter
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
    
    // Handle Fire TV menu button (3 lines button)
    override fun onResume() {
        super.onResume()
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
                showOptionsMenu()
                true
            } else {
                false
            }
        }
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
