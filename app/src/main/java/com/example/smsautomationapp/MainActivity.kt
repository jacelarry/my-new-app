package com.example.smsautomationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.smsautomationapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var smsCount = 5000
    private var tokenCount = 10
    private var completed = 5103
    private var failed = 651

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        try {
            Log.d("MainActivity", "Inflating binding...")
            binding = ActivityMainBinding.inflate(layoutInflater)
            Log.d("MainActivity", "Binding inflated, setting content view...")
            setContentView(binding.root)
            Log.d("MainActivity", "Content view set successfully")

            setupClickListeners()
            autoUpdateData()

            Log.d("MainActivity", "autoUpdateData started")
        } catch (e: Exception) {
            Log.e("MainActivity", "FATAL ERROR in onCreate", e)
            e.printStackTrace()
            // Rethrow to see the crash in logcat
            throw e
        }
    }

    private fun setupClickListeners() {
        // Manage button - could open campaigns or settings
        binding.manageBtn.setOnClickListener {
            Log.d("MainActivity", "Manage button clicked")
            Toast.makeText(this, "Opening Campaigns...", Toast.LENGTH_SHORT).show()
            // You can implement campaign management here
            // For now, show a toast
        }

        // Import button - open import/contacts functionality
        binding.importBtn.setOnClickListener {
            Log.d("MainActivity", "Import button clicked")
            startActivity(Intent(this, ImportActivity::class.java))
        }

        // Contacts button - could show contacts list
        binding.contactsBtn.setOnClickListener {
            Log.d("MainActivity", "Contacts button clicked")
            Toast.makeText(this, "Contacts feature - Coming soon!", Toast.LENGTH_SHORT).show()
            // TODO: Implement contacts viewing
            // For example: startActivity(Intent(this, ContactsActivity::class.java))
        }

        // Completed button - show completed messages
        binding.completedBtn.setOnClickListener {
            Log.d("MainActivity", "Completed button clicked")
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("INITIAL_TAB", 0) // Completed tab
            startActivity(intent)
        }

        // Failed button - show failed messages
        binding.failedBtn.setOnClickListener {
            Log.d("MainActivity", "Failed button clicked")
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("INITIAL_TAB", 2) // Failed tab
            startActivity(intent)
        }

        // View All text - show all transaction history
        binding.viewAll.setOnClickListener {
            Log.d("MainActivity", "View All clicked")
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // FAB - Start automation or open subscription
        binding.fab.setOnClickListener {
            Log.d("MainActivity", "FAB clicked")
            Toast.makeText(this, "Opening Subscription Plans...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SubscriptionActivity::class.java))
        }
    }

    private fun autoUpdateData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    try {
                        delay(5000)
                        // Tokens auto update
                        tokenCount = (tokenCount + Random.nextInt(0, 3)).coerceAtMost(100)
                        binding.tokenUnits.text = "$tokenCount Units"
                        binding.tokenUpdated.text = "updated just now"

                        // SMS update logic
                        if (smsCount > 0) {
                            smsCount -= Random.nextInt(0, 10)
                            binding.smsStatus.text = "$smsCount left"
                        } else {
                            binding.smsStatus.text = "Unlimited"
                        }
                        binding.smsUpdated.text = "updated now"

                        // Stats update
                        completed += Random.nextInt(1, 10)
                        failed += Random.nextInt(0, 5)
                        binding.completedBtn.text = "$completed Completed"
                        binding.failedBtn.text = "$failed Failed"
                    } catch (e: Exception) {
                        Log.e("MainActivity", "autoUpdateData loop error", e)
                    }
                }
            }
        }
    }
}