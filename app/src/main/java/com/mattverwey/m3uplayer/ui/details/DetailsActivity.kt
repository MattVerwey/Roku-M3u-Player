package com.mattverwey.m3uplayer.ui.details

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.target.Target
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.data.model.Channel
import com.mattverwey.m3uplayer.repository.ChannelRepository
import com.mattverwey.m3uplayer.ui.playback.PlaybackActivity
import kotlinx.coroutines.*

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
    private lateinit var detailsOverview: DetailsOverviewRow
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    companion object {
        private const val ARG_CHANNEL = "channel"
        private const val ACTION_WATCH = 1L
        private const val ACTION_FAVORITE = 2L
        
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
        detailsOverview = DetailsOverviewRow(channel)
        
        // Set image dimensions for better display (16:9 aspect ratio for movies/series)
        val imageWidth = resources.getDimensionPixelSize(R.dimen.details_poster_width)
        val imageHeight = resources.getDimensionPixelSize(R.dimen.details_poster_height)
        
        // Load poster image asynchronously
        if (!channel.logoUrl.isNullOrEmpty()) {
            loadPosterImage(channel.logoUrl!!, imageWidth, imageHeight)
        } else {
            // Set a placeholder image if no logo URL is available
            detailsOverview.imageDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_placeholder)
        }
        
        // Add action buttons
        detailsOverview.addAction(Action(ACTION_WATCH, getString(R.string.watch_now)))
        
        // Add favorites button
        val isFavorite = repository.isFavorite(channel.id)
        val favoriteText = if (isFavorite) getString(R.string.remove_from_favorites) else getString(R.string.add_to_favorites)
        detailsOverview.addAction(Action(ACTION_FAVORITE, favoriteText))
        
        val presenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        presenter.backgroundColor = ContextCompat.getColor(requireContext(), R.color.card_background)
        presenter.initialState = FullWidthDetailsOverviewRowPresenter.STATE_HALF
        
        val rowsAdapter = ArrayObjectAdapter(presenter)
        rowsAdapter.add(detailsOverview)
        
        // Add related content section if available
        setupRelatedContent(rowsAdapter)
        
        adapter = rowsAdapter
        
        // Handle action clicks
        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Action -> handleActionClick(item)
            }
        }
    }
    
    private fun loadPosterImage(url: String, width: Int, height: Int) {
        coroutineScope.launch {
            try {
                val imageLoader = ImageLoader(requireContext())
                val request = ImageRequest.Builder(requireContext())
                    .data(url)
                    .size(width, height)
                    .target(object : Target {
                        override fun onSuccess(result: Drawable) {
                            detailsOverview.imageDrawable = result
                        }
                        
                        override fun onError(error: Drawable?) {
                            // Use placeholder on error
                            detailsOverview.imageDrawable = ContextCompat.getDrawable(
                                requireContext(), 
                                R.drawable.ic_placeholder
                            )
                        }
                    })
                    .build()
                
                imageLoader.execute(request)
            } catch (e: Exception) {
                // Fallback to placeholder on exception
                detailsOverview.imageDrawable = ContextCompat.getDrawable(
                    requireContext(), 
                    R.drawable.ic_placeholder
                )
            }
        }
    }
    
    private fun setupRelatedContent(rowsAdapter: ArrayObjectAdapter) {
        // This can be extended to show related movies, series episodes, etc.
        // For now, we'll leave it empty but the structure is in place
    }
    
    private fun handleActionClick(action: Action) {
        when (action.id) {
            ACTION_WATCH -> playChannel()
            ACTION_FAVORITE -> toggleFavorite()
        }
    }
    
    private fun toggleFavorite() {
        val isFavorite = repository.isFavorite(channel.id)
        
        if (isFavorite) {
            repository.removeFromFavorites(channel.id)
        } else {
            repository.addToFavorites(channel.id)
        }
        
        // Update the action button text
        val newFavoriteText = if (!isFavorite) 
            getString(R.string.remove_from_favorites) 
        else 
            getString(R.string.add_to_favorites)
        
        // Update the action in the overview row
        detailsOverview.actionsAdapter.apply {
            for (i in 0 until size()) {
                val item = get(i)
                if (item is Action && item.id == ACTION_FAVORITE) {
                    remove(item)
                    add(i, Action(ACTION_FAVORITE, newFavoriteText))
                    break
                }
            }
        }
    }
    
    private fun playChannel() {
        repository.addToRecentlyWatched(channel.id)
        
        val intent = Intent(requireContext(), PlaybackActivity::class.java).apply {
            putExtra(PlaybackActivity.EXTRA_CHANNEL, channel)
        }
        startActivity(intent)
    }
    
    inner class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
        override fun onBindDescription(vh: ViewHolder, item: Any) {
            val channel = item as Channel
            vh.title.text = channel.name
            
            // Build a rich subtitle with available metadata
            val subtitleParts = mutableListOf<String>()
            channel.groupTitle?.let { subtitleParts.add(it) }
            channel.releaseDate?.let { subtitleParts.add(it) }
            channel.rating?.let { subtitleParts.add("★ $it") }
            
            vh.subtitle.text = subtitleParts.joinToString(" • ")
            
            // Enhanced description with metadata
            val descriptionBuilder = StringBuilder()
            channel.description?.let { descriptionBuilder.append(it) }
            
            // Add genre information
            channel.genre?.let { 
                if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append("\n\n")
                descriptionBuilder.append("${getString(R.string.genre)}: $it")
            }
            
            // Add duration for movies
            channel.duration?.let { durationMinutes ->
                if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append("\n")
                val hours = durationMinutes / 60
                val minutes = durationMinutes % 60
                val durationText = if (hours > 0) {
                    "${hours}h ${minutes}m"
                } else {
                    "${minutes}m"
                }
                descriptionBuilder.append("${getString(R.string.duration)}: $durationText")
            }
            
            vh.body.text = if (descriptionBuilder.isEmpty()) {
                getString(R.string.no_description)
            } else {
                descriptionBuilder.toString()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
