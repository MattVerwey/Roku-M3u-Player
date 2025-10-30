package com.mattverwey.m3uplayer.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.ui.browse.BrowseFragment
import com.mattverwey.m3uplayer.ui.login.LoginActivity

class MainActivity : FragmentActivity() {
    
    private lateinit var cacheManager: CacheManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        cacheManager = CacheManager(this)
        
        if (savedInstanceState == null) {
            checkLoginStatus()
        }
    }
    
    private fun checkLoginStatus() {
        val hasM3U = cacheManager.getM3UUrl() != null
        val hasXtream = cacheManager.getXtreamCredentials() != null
        
        if (!hasM3U && !hasXtream) {
            // Show login screen
            LoginActivity.start(this)
            finish()
        } else {
            // Show browse screen
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, BrowseFragment())
                .commit()
        }
    }
}
