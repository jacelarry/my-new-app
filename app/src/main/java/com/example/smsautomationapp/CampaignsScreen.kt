package com.example.smsautomationapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CampaignsScreen() {
    var interactiveMessage by remember { mutableStateOf("Dear customer, please reply with:\n1. For Bundle 1\n2. For Bundle 2\n3. For Bundle 3") }
    var regularMessage by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Interactive") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Regular") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> OutlinedTextField(
                value = interactiveMessage,
                onValueChange = { interactiveMessage = it },
                label = { Text("Interactive SMS Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 10
            )
            1 -> OutlinedTextField(
                value = regularMessage,
                onValueChange = { regularMessage = it },
                label = { Text("Regular SMS Message") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 10
            )
        }
    }
}
