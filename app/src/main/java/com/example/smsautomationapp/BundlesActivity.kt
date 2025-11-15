package com.example.smsautomationapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smsautomationapp.databinding.ActivityBundlesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BundlesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBundlesBinding
    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBundlesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.purchaseButton.setOnClickListener {
            val phoneNumber = binding.phoneNumberEditText.text.toString()
            val amount = binding.amountEditText.text.toString()

            if (phoneNumber.isNotBlank() && amount.isNotBlank()) {
                val consumerKey = TillPrefs.getConsumerKey(this)
                val consumerSecret = TillPrefs.getConsumerSecret(this)
                val passkey = TillPrefs.getPasskey(this)
                val tillNumber = TillPrefs.getTill(this)

                if (consumerKey != null && consumerSecret != null && passkey != null && tillNumber != null) {
                    val darajaApi = DarajaApi(consumerKey, consumerSecret, passkey, tillNumber)
                    uiScope.launch {
                        val success = darajaApi.initiateStkPush(phoneNumber, amount.toInt(), tillNumber)
                        if (success) {
                            Toast.makeText(this@BundlesActivity, "STK push initiated.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@BundlesActivity, "Failed to initiate STK push.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please set your credentials first.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}