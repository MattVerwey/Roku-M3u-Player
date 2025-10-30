package com.mattverwey.m3uplayer.ui

import android.app.AlertDialog
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
        
        try {
            cacheManager = CacheManager(this)
            
            if (savedInstanceState == null) {
                checkLoginStatus()
            }
        } catch (e: SecurityException) {
            // Show error dialog and exit if encryption is not available
            showEncryptionUnavailableDialog(e.message ?: "Encryption not available")
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
    
    private fun showEncryptionUnavailableDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Security Feature Unavailable")
            .setMessage("This app requires secure storage to protect your credentials, but your device does not support it.\n\n$message")
            .setCancelable(false)
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .show()
    }
}
