package com.mattverwey.m3uplayer.ui.browse

import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.launch

/**
 * Fragment to browse channels within a specific category type.
 * Shows all categories for the selected type (Live TV, Movies, or Series)
 * with channels sorted appropriately.
 */
class CategoryBrowseFragment : BrowseSupportFragment() {
    
    private lateinit var repository: ChannelRepository
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var categoryType: ChannelCategory
    
    companion object {
        private const val ARG_CATEGORY_TYPE = "arg_category_type"
        
        fun newInstance(categoryType: ChannelCategory): CategoryBrowseFragment {
            return CategoryBrowseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY_TYPE, categoryType.name)
                }
            }
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        categoryType = arguments?.getString(ARG_CATEGORY_TYPE)?.let {
            ChannelCategory.valueOf(it)
        } ?: ChannelCategory.LIVE_TV
        
        setupUI()
        repository = ChannelRepository(CacheManager(requireContext()))
        loadChannels()
        setupEventListeners()
    }
    
    private fun setupUI() {
        // Set title based on category type
        title = when (categoryType) {
            ChannelCategory.LIVE_TV -> "Live TV"
            ChannelCategory.MOVIE -> "Movies"
            ChannelCategory.SERIES -> "Series"
            ChannelCategory.RECENTLY_WATCHED -> "Recently Watched"
        }
        
        badgeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_placeholder)
        
        // Enable headers (left sidebar)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        
        // Set colors
        brandColor = android.graphics.Color.parseColor("#0D0D0D")
        searchAffordanceColor = android.graphics.Color.WHITE
        
        // Disable search
        setOnSearchClickedListener {
            Toast.makeText(requireContext(), "Search not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupEventListeners() {
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Channel) {
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
        
        // Filter channels by category type
        val filteredChannels = channels.filter { it.category == categoryType }
        
        // Group by category (groupTitle)
        val categoriesMap = filteredChannels.groupBy { it.groupTitle ?: "Uncategorized" }
        
        // Sort and display based on category type
        when (categoryType) {
            ChannelCategory.LIVE_TV -> {
                // Live TV: Sort categories alphabetically, channels by channel number/order
                categoriesMap.entries
                    .sortedBy { it.key }
                    .forEach { (categoryName, categoryChannels) ->
                        // Sort channels by name (which often includes channel numbers)
                        val sortedChannels = categoryChannels.sortedBy { 
                            // Try to extract channel number from name, otherwise use name
                            val channelNumber = it.name.filter { c -> c.isDigit() }.toIntOrNull()
                            channelNumber ?: Int.MAX_VALUE
                        }
                        addRow("📺 $categoryName", sortedChannels)
                    }
            }
            ChannelCategory.MOVIE, ChannelCategory.SERIES -> {
                // Movies/Series: Sort categories alphabetically, content alphabetically
                categoriesMap.entries
                    .sortedBy { it.key }
                    .forEach { (categoryName, categoryChannels) ->
                        val sortedChannels = categoryChannels.sortedBy { it.name }
                        val icon = if (categoryType == ChannelCategory.MOVIE) "🎬" else "📺"
                        addRow("$icon $categoryName", sortedChannels)
                    }
            }
            ChannelCategory.RECENTLY_WATCHED -> {
                // This shouldn't happen, but handle it
                addRow("Recently Watched", filteredChannels)
            }
        }
        
        adapter = rowsAdapter
        
        // Start with focus on first item
        if (rowsAdapter.size() > 0) {
            selectedPosition = 0
        }
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
}
