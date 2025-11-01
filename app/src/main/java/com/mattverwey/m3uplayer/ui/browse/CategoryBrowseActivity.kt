package com.mattverwey.m3uplayer.ui.browse

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.model.ChannelCategory

/**
 * Activity to display channels organized by categories.
 * Shows all categories for a specific content type (Live TV, Movies, or Series).
 */
class CategoryBrowseActivity : FragmentActivity() {
    
    companion object {
        private const val EXTRA_CATEGORY_TYPE = "extra_category_type"
        
        fun start(context: Context, categoryType: ChannelCategory) {
            val intent = Intent(context, CategoryBrowseActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_TYPE, categoryType.name)
            }
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val categoryTypeName = intent.getStringExtra(EXTRA_CATEGORY_TYPE)
        val categoryType = categoryTypeName?.let { 
            ChannelCategory.valueOf(it) 
        } ?: ChannelCategory.LIVE_TV
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, CategoryBrowseFragment.newInstance(categoryType))
                .commit()
        }
    }
}
