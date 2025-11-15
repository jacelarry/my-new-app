package com.example.smsautomationapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smsautomationapp.databinding.ActivityComposeMessageBinding

class ComposeMessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComposeMessageBinding
    private var messageType: String = "REGULAR"
    private var contactsCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComposeMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageType = intent.getStringExtra("MESSAGE_TYPE") ?: "REGULAR"
        contactsCount = intent.getIntExtra("CONTACTS_COUNT", 0)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        when (messageType) {
            "INTERACTIVE" -> {
                binding.toolbar.title = "Compose Interactive SMS"
                binding.messageHintText.text = "Create an interactive message with reply options"

                // Set sample interactive message
                binding.messageEditText.setText(
                    "Dear customer, please reply with:\n" +
                    "1. For Daily Bundle (Ksh 60)\n" +
                    "2. For Weekly Bundle (Ksh 250)\n" +
                    "3. For Monthly Bundle (Ksh 550)\n" +
                    "4. To unsubscribe"
                )

                binding.messageTypeLabel.text = "Interactive SMS"
            }
            "REGULAR" -> {
                binding.toolbar.title = "Compose Regular SMS"
                binding.messageHintText.text = "Create a regular broadcast message"

                // Set sample regular message
                binding.messageEditText.setText(
                    "Hello! Thank you for choosing our service. " +
                    "Special offer: Get 20% off on all bundles this week!"
                )

                binding.messageTypeLabel.text = "Regular SMS"
            }
        }

        binding.recipientCountText.text = "Recipients: $contactsCount contacts"
    }

    private fun setupClickListeners() {
        binding.sendBtn.setOnClickListener {
            val message = binding.messageEditText.text.toString()

            if (message.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contactsCount == 0) {
                Toast.makeText(this, "No contacts to send to", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Implement actual SMS sending logic
            Toast.makeText(
                this,
                "Sending $messageType message to $contactsCount contacts...",
                Toast.LENGTH_LONG
            ).show()

            // Simulate sending delay
            binding.sendBtn.isEnabled = false
            binding.sendBtn.text = "Sending..."

            // In real app, this would trigger SMS sending service
            binding.root.postDelayed({
                Toast.makeText(this, "Messages sent successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }, 2000)
        }

        binding.previewBtn.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            Toast.makeText(
                this,
                "Preview:\n$message\n\nLength: ${message.length} characters",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}

