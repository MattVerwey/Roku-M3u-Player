package com.mattverwey.m3uplayer.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.databinding.ActivitySettingsBinding
import com.mattverwey.m3uplayer.ui.login.LoginActivity

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var cacheManager: CacheManager
    
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        try {
            cacheManager = CacheManager(this)
            
            setupUI()
            setupListeners()
        } catch (e: SecurityException) {
            // Show error dialog and exit if encryption is not available
            showEncryptionUnavailableDialog(e.message ?: "Encryption not available")
        }
    }
    
    private fun setupUI() {
        // Set current tracking preference
        binding.trackingSwitch.isChecked = cacheManager.isTrackingEnabled()
        
        // Show current login source
        val sourceText = when (cacheManager.getSourceType()) {
            CacheManager.SourceType.M3U -> "M3U Playlist"
            CacheManager.SourceType.XTREAM -> "Xtream Codes"
        }
        binding.currentSourceText.text = sourceText
    }
    
    private fun setupListeners() {
        // Tracking toggle
        binding.trackingSwitch.setOnCheckedChangeListener { _, isChecked ->
            cacheManager.setTrackingEnabled(isChecked)
            val message = if (isChecked) {
                "Viewing history tracking enabled"
            } else {
                "Viewing history tracking disabled and cleared"
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        
        // Clear viewing history
        binding.clearHistoryButton.setOnClickListener {
            showClearHistoryDialog()
        }
        
        // Clear cache
        binding.clearCacheButton.setOnClickListener {
            showClearCacheDialog()
        }
        
        // Logout
        binding.logoutButton.setOnClickListener {
            showLogoutDialog()
        }
        
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun showClearHistoryDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear Viewing History")
            .setMessage("This will permanently delete your viewing history. Continue?")
            .setPositiveButton("Clear") { _, _ ->
                cacheManager.clearRecentlyWatched()
                Toast.makeText(this, "Viewing history cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showClearCacheDialog() {
        AlertDialog.Builder(this)
            .setTitle("Clear Cache")
            .setMessage("This will clear cached channels and EPG data. You will need to reload playlists. Continue?")
            .setPositiveButton("Clear") { _, _ ->
                cacheManager.clearCache()
                Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("This will clear all credentials and data. You will need to login again. Continue?")
            .setPositiveButton("Logout") { _, _ ->
                performSecureLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun performSecureLogout() {
        cacheManager.secureLogout()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate back to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
