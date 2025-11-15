package com.example.smsautomationapp

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        const val ACTION_STATUS_UPDATE = "com.example.smsautomationapp.ACTION_STATUS_UPDATE"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            
            // Extract the status and message from the data payload
            val status = remoteMessage.data["status"]
            val message = remoteMessage.data["message"]
            val fullStatus = if (status != null && message != null) "$status: $message" else status ?: "No status received"

            // Store the transaction status
            TillPrefs.setLastTransactionStatus(this, fullStatus)

            // Broadcast the status update to MainActivity
            val intent = Intent().apply {
                action = ACTION_STATUS_UPDATE
                putExtra("status", fullStatus)
            }
            sendBroadcast(intent)

            // TODO: You could also create and show a local notification here
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        // e.g., sendRegistrationToServer(token)
    }
}
