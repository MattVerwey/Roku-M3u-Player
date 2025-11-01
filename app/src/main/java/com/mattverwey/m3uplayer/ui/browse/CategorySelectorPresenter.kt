package com.mattverwey.m3uplayer.ui.browse

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.Presenter
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.CategorySelector

/**
 * Presenter for displaying category selector cards.
 */
class CategorySelectorPresenter : Presenter() {
    
    companion object {
        private const val CARD_WIDTH = 313
        private const val CARD_HEIGHT = 176
    }
    
    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        val cardView = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(CARD_WIDTH, CARD_HEIGHT)
            isFocusable = true
            isFocusableInTouchMode = true
            gravity = android.view.Gravity.CENTER
            textSize = 24f
            setTextColor(Color.WHITE)
            background = ContextCompat.getDrawable(context, R.drawable.ic_placeholder)
            setPadding(16, 16, 16, 16)
        }
        
        return Presenter.ViewHolder(cardView)
    }
    
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any?) {
        val selector = item as CategorySelector
        val cardView = viewHolder.view as TextView
        
        cardView.text = "${selector.icon}\n${selector.title}"
    }
    
    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        // Nothing to unbind
    }
}
