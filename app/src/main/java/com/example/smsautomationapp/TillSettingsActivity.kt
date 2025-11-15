package com.example.smsautomationapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TillSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_till_settings)

        val tillEt = findViewById<EditText>(R.id.etTill)
        val consumerKeyEt = findViewById<EditText>(R.id.etConsumerKey)
        val consumerSecretEt = findViewById<EditText>(R.id.etConsumerSecret)
        val passkeyEt = findViewById<EditText>(R.id.etPasskey)
        val saveBtn = findViewById<Button>(R.id.btnSave)

        TillPrefs.getTill(this)?.let { tillEt.setText(it) }
        TillPrefs.getConsumerKey(this)?.let { consumerKeyEt.setText(it) }
        TillPrefs.getConsumerSecret(this)?.let { consumerSecretEt.setText(it) }
        TillPrefs.getPasskey(this)?.let { passkeyEt.setText(it) }

        saveBtn.setOnClickListener {
            val till = tillEt.text?.toString()?.trim().orEmpty()
            val consumerKey = consumerKeyEt.text?.toString()?.trim().orEmpty()
            val consumerSecret = consumerSecretEt.text?.toString()?.trim().orEmpty()
            val passkey = passkeyEt.text?.toString()?.trim().orEmpty()

            if (till.isBlank() || consumerKey.isBlank() || consumerSecret.isBlank() || passkey.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                TillPrefs.setTill(this, till)
                TillPrefs.setConsumerKey(this, consumerKey)
                TillPrefs.setConsumerSecret(this, consumerSecret)
                TillPrefs.setPasskey(this, passkey)
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}