package com.mattverwey.m3uplayer.ui.browse

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.widget.Presenter
import coil.load
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.Channel

class ChannelCardPresenter : Presenter() {
    
    private val cardWidth: Int
        get() = 280
    
    private val cardHeight: Int
        get() = 360
    
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        // Create a custom card view to avoid Leanback ImageCardView layout inflation bug
        val cardView = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(cardWidth, cardHeight).apply {
                setMargins(12, 12, 12, 12)
            }
            isFocusable = true
            isFocusableInTouchMode = true
            setBackgroundColor(Color.parseColor("#2A2A2A"))
            elevation = 4f
            
            // Add focus change listener for better visual feedback
            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.scaleX = 1.1f
                    view.scaleY = 1.1f
                    view.elevation = 16f
                    view.setBackgroundColor(Color.parseColor("#3A3A3A"))
                } else {
                    view.scaleX = 1.0f
                    view.scaleY = 1.0f
                    view.elevation = 4f
                    view.setBackgroundColor(Color.parseColor("#2A2A2A"))
                }
            }
        }
        
        // Image container
        val imageView = ImageView(parent.context).apply {
            id = R.id.card_image
            layoutParams = FrameLayout.LayoutParams(cardWidth, (cardHeight * 0.7).toInt()).apply {
                gravity = Gravity.TOP
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        cardView.addView(imageView)
        
        // Info area
        val infoLayout = LinearLayout(parent.context).apply {
            id = R.id.card_info
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (cardHeight * 0.3).toInt()
            ).apply {
                gravity = Gravity.BOTTOM
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1E1E1E"))
            setPadding(12, 12, 12, 12)
        }
        
        // Title
        val titleView = TextView(parent.context).apply {
            id = R.id.card_title
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setTextColor(Color.WHITE)
            textSize = 16f
            maxLines = 2
            setShadowLayer(2f, 0f, 2f, Color.BLACK)
        }
        infoLayout.addView(titleView)
        
        // Subtitle
        val subtitleView = TextView(parent.context).apply {
            id = R.id.card_subtitle
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 4
            }
            setTextColor(Color.parseColor("#CCCCCC"))
            textSize = 13f
            maxLines = 1
            setShadowLayer(2f, 0f, 2f, Color.BLACK)
        }
        infoLayout.addView(subtitleView)
        
        cardView.addView(infoLayout)
        
        return Presenter.ViewHolder(cardView)
    }
    
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val channel = item as Channel
        val cardView = viewHolder.view as FrameLayout
        
        val imageView = cardView.findViewById<ImageView>(R.id.card_image)
        val titleView = cardView.findViewById<TextView>(R.id.card_title)
        val subtitleView = cardView.findViewById<TextView>(R.id.card_subtitle)
        
        titleView.text = channel.name
        subtitleView.text = channel.groupTitle ?: ""
        
        // Load image or use placeholder
        if (!channel.logoUrl.isNullOrEmpty()) {
            imageView.load(channel.logoUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_placeholder)
        }
    }
    
    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        val cardView = viewHolder.view as FrameLayout
        val imageView = cardView.findViewById<ImageView>(R.id.card_image)
        imageView.setImageDrawable(null)
    }
}
