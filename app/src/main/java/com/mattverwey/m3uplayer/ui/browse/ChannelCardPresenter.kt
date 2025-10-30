package com.mattverwey.m3uplayer.ui.browse

import android.graphics.Color
import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.Channel
import kotlin.math.roundToInt

class ChannelCardPresenter : Presenter() {
    
    private val cardWidth: Int
        get() = 240
    
    private val cardHeight: Int
        get() = 320
    
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(cardWidth, cardHeight)
        }
        return ViewHolder(cardView)
    }
    
    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val cardView = viewHolder.view as ImageCardView
        
        cardView.titleText = channel.name
        cardView.contentText = channel.groupTitle ?: ""
        
        // Load image or use placeholder
        if (!channel.logoUrl.isNullOrEmpty()) {
            cardView.mainImageView.load(channel.logoUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        } else {
            cardView.mainImageView.setImageResource(R.drawable.ic_placeholder)
        }
        
        // Set card background
        cardView.setInfoAreaBackgroundColor(Color.parseColor("#1E1E1E"))
    }
    
    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val cardView = viewHolder.view as ImageCardView
        cardView.badgeImage = null
        cardView.mainImage = null
    }
}
