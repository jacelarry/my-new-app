package com.example.smsautomationapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smsautomationapp.databinding.ActivitySubscriptionBinding
import kotlinx.coroutines.launch

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySubscriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dailyPlanCard.setOnClickListener {
            purchasePlan("Daily", 60)
        }

        binding.weeklyPlanCard.setOnClickListener {
            purchasePlan("Weekly", 250)
        }

        binding.monthlyPlanCard.setOnClickListener {
            purchasePlan("Monthly", 550)
        }
    }

    private fun purchasePlan(plan: String, price: Int) {
        // This is a placeholder phone number. You will need to get the actual user's number.
        val phoneNumber = "254712345678"
        val consumerKey = TillPrefs.getConsumerKey(this)
        val consumerSecret = TillPrefs.getConsumerSecret(this)
        val passkey = TillPrefs.getPasskey(this)
        val tillNumber = TillPrefs.getTill(this)

        if (consumerKey != null && consumerSecret != null && passkey != null && tillNumber != null) {
            val darajaApi = DarajaApi(consumerKey, consumerSecret, passkey, tillNumber)
            lifecycleScope.launch {
                val success = darajaApi.initiateStkPush(phoneNumber, price, tillNumber)
                if (success) {
                    Toast.makeText(this@SubscriptionActivity, "STK push initiated for $plan plan.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SubscriptionActivity, "Failed to initiate STK push.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please set your credentials first.", Toast.LENGTH_LONG).show()
        }
    }
}