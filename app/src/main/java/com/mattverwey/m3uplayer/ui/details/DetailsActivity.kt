package com.mattverwey.m3uplayer.ui.details

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import coil.load
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.repository.ChannelRepository
import com.mattverwey.m3uplayer.ui.playback.PlaybackActivity

class DetailsActivity : FragmentActivity() {
    
    companion object {
        const val EXTRA_CHANNEL = "extra_channel"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_CHANNEL, Channel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_CHANNEL)
        }
        
        if (channel != null && savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_container, DetailsFragment.newInstance(channel))
                .commit()
        }
    }
}

class DetailsFragment : DetailsSupportFragment() {
    
    private lateinit var channel: Channel
    private lateinit var repository: ChannelRepository
    
    companion object {
        private const val ARG_CHANNEL = "channel"
        
        fun newInstance(channel: Channel): DetailsFragment {
            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_CHANNEL, channel)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_CHANNEL, Channel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_CHANNEL)
        } ?: run {
            requireActivity().finish()
            return
        }
        
        repository = ChannelRepository(CacheManager(requireContext()))
        setupDetailsOverviewRow()
    }
    
    private fun setupDetailsOverviewRow() {
        val detailsOverview = DetailsOverviewRow(channel)
        
        // Load poster image
        if (!channel.logoUrl.isNullOrEmpty()) {
            // Note: In a real app, you'd want to load this asynchronously
            // For simplicity, we're using a placeholder approach
        }
        
        // Add action buttons
        detailsOverview.addAction(Action(1, getString(R.string.watch_now)))
        
        val presenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        presenter.backgroundColor = resources.getColor(R.color.card_background, null)
        
        val rowsAdapter = ArrayObjectAdapter(presenter)
        rowsAdapter.add(detailsOverview)
        
        adapter = rowsAdapter
        
        // Handle action clicks
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            try {
                if (item is Action && item.id == 1L) {
                    playChannel()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun playChannel() {
        // Validate stream URL before attempting playback
        if (channel.streamUrl.isEmpty()) {
            android.widget.Toast.makeText(
                requireContext(),
                "Error: Stream URL not available for this content",
                android.widget.Toast.LENGTH_LONG
            ).show()
            return
        }
        
        repository.addToRecentlyWatched(channel.id)
        
        try {
            val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
                putExtra(PlaybackActivity.EXTRA_CHANNEL, channel)
            }
            startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(
                requireContext(),
                "Error starting playback: ${e.message}",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }
    
    inner class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
        override fun onBindDescription(vh: ViewHolder, item: Any) {
            val channel = item as Channel
            vh.title.text = channel.name
            vh.subtitle.text = channel.groupTitle
            vh.body.text = channel.description ?: getString(R.string.no_description)
        }
    }
}
