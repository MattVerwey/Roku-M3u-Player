package com.mattverwey.m3uplayer.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mattverwey.m3uplayer.R
import com.mattverwey.m3uplayer.data.cache.CacheManager
import com.mattverwey.m3uplayer.data.model.XtreamCredentials
import com.mattverwey.m3uplayer.databinding.ActivityLoginBinding
import com.mattverwey.m3uplayer.repository.ChannelRepository
import com.mattverwey.m3uplayer.ui.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var repository: ChannelRepository
    private lateinit var cacheManager: CacheManager
    
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        cacheManager = CacheManager(this)
        repository = ChannelRepository(cacheManager)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        // Pre-fill if values exist
        cacheManager.getM3UUrl()?.let {
            binding.m3uUrlInput.setText(it)
            binding.loginTypeM3u.isChecked = true
        }
        
        cacheManager.getXtreamCredentials()?.let {
            binding.xtreamServerInput.setText(it.serverUrl)
            binding.xtreamUsernameInput.setText(it.username)
            binding.xtreamPasswordInput.setText(it.password)
            binding.loginTypeXtream.isChecked = true
        }
        
        updateInputVisibility()
    }
    
    private fun setupListeners() {
        binding.loginTypeGroup.setOnCheckedChangeListener { _, checkedId ->
            updateInputVisibility()
        }
        
        binding.loginButton.setOnClickListener {
            when (binding.loginTypeGroup.checkedRadioButtonId) {
                R.id.login_type_m3u -> loginWithM3U()
                R.id.login_type_xtream -> loginWithXtream()
                else -> Toast.makeText(this, "Please select a login type", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateInputVisibility() {
        when (binding.loginTypeGroup.checkedRadioButtonId) {
            R.id.login_type_m3u -> {
                binding.m3uInputLayout.visibility = View.VISIBLE
                binding.xtreamInputLayout.visibility = View.GONE
            }
            R.id.login_type_xtream -> {
                binding.m3uInputLayout.visibility = View.GONE
                binding.xtreamInputLayout.visibility = View.VISIBLE
            }
        }
    }
    
    private fun loginWithM3U() {
        val url = binding.m3uUrlInput.text.toString().trim()
        
        if (url.isEmpty()) {
            Toast.makeText(this, "Please enter M3U URL", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoading(true)
        
        repository.setM3UUrl(url)
        
        lifecycleScope.launch {
            val result = repository.loadChannels(forceRefresh = true)
            
            result.onSuccess {
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()
                navigateToMain()
            }.onFailure { error ->
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun loginWithXtream() {
        val server = binding.xtreamServerInput.text.toString().trim()
        val username = binding.xtreamUsernameInput.text.toString().trim()
        val password = binding.xtreamPasswordInput.text.toString().trim()
        
        if (server.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Ensure server URL has protocol
        val serverUrl = if (!server.startsWith("http")) {
            "http://$server"
        } else {
            server
        }
        
        showLoading(true)
        
        val credentials = XtreamCredentials(serverUrl, username, password)
        
        lifecycleScope.launch {
            val result = repository.authenticateXtream(credentials)
            
            result.onSuccess {
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_success),
                    Toast.LENGTH_SHORT
                ).show()
                navigateToMain()
            }.onFailure { error ->
                showLoading(false)
                Toast.makeText(
                    this@LoginActivity,
                    "Login failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
