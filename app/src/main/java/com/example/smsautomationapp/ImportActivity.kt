package com.example.smsautomationapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.smsautomationapp.databinding.ActivityImportBinding
import com.example.smsautomationapp.ui.ImportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.collectLatest
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImportBinding
    private val importedContacts = mutableListOf<Contact>()
    private val excelImportedContacts = mutableListOf<Contact>()
    private val phoneImportedContacts = mutableListOf<Contact>()

    // Excel/CSV file picker
    private val csvPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri -> importFromCsv(uri) }
        }
    }

    // Permission request
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openContactsMultiPicker()
        } else {
            Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show()
        }
    }

    private val viewModel: ImportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeContacts()
        setupClickListeners()
    }

    private fun observeContacts() {
        lifecycleScope.launch {
            viewModel.csvFlow.collectLatest { csv ->
                excelImportedContacts.clear()
                excelImportedContacts.addAll(csv.map { Contact(it.name, it.phone, ContactSource.CSV) })
                updateContactCount()
            }
        }
        lifecycleScope.launch {
            viewModel.phoneFlow.collectLatest { phone ->
                phoneImportedContacts.clear()
                phoneImportedContacts.addAll(phone.map { Contact(it.name, it.phone, ContactSource.PHONE) })
                updateContactCount()
            }
        }
        lifecycleScope.launch {
            viewModel.allFlow.collectLatest { all ->
                importedContacts.clear()
                importedContacts.addAll(all.map { entity ->
                    val source = if (entity.source == "CSV") ContactSource.CSV else ContactSource.PHONE
                    Contact(entity.name, entity.phone, source)
                })
                updateContactCount()
            }
        }
    }

    private fun setupClickListeners() {
        // Excel/CSV import - also toggle expand/collapse
        binding.btnImportCsv.setOnClickListener {
            toggleSection("csv")
            openCsvPicker()
        }

        // Phone contacts import (multi-select) - also toggle expand/collapse
        binding.btnImportContacts.setOnClickListener {
            toggleSection("phone")
            if (checkContactsPermission()) openContactsMultiPicker() else requestContactsPermission()
        }

        // CSV Interactive: Send/Cancel
        binding.btnExcelInteractiveSend.setOnClickListener {
            val msg = binding.etExcelInteractiveMsg.text.toString()
            if (msg.isBlank()) { Toast.makeText(this, getString(R.string.toast_enter_message), Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            confirmAndSend(contacts = excelImportedContacts, interactiveMsg = msg, regularMsg = null, section = "CSV")
        }
        binding.btnExcelInteractiveCancel.setOnClickListener { binding.etExcelInteractiveMsg.setText("") }

        // CSV Regular: Send/Cancel
        binding.btnExcelRegularSend.setOnClickListener {
            val msg = binding.etExcelRegularMsg.text.toString()
            if (msg.isBlank()) { Toast.makeText(this, getString(R.string.toast_enter_message), Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            confirmAndSend(contacts = excelImportedContacts, interactiveMsg = null, regularMsg = msg, section = "CSV")
        }
        binding.btnExcelRegularCancel.setOnClickListener { binding.etExcelRegularMsg.setText("") }

        // Phone Interactive: Send/Cancel
        binding.btnPhoneInteractiveSend.setOnClickListener {
            val msg = binding.etPhoneInteractiveMsg.text.toString()
            if (msg.isBlank()) { Toast.makeText(this, getString(R.string.toast_enter_message), Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            confirmAndSend(contacts = phoneImportedContacts, interactiveMsg = msg, regularMsg = null, section = "Phone")
        }
        binding.btnPhoneInteractiveCancel.setOnClickListener { binding.etPhoneInteractiveMsg.setText("") }

        // Phone Regular: Send/Cancel
        binding.btnPhoneRegularSend.setOnClickListener {
            val msg = binding.etPhoneRegularMsg.text.toString()
            if (msg.isBlank()) { Toast.makeText(this, getString(R.string.toast_enter_message), Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            confirmAndSend(contacts = phoneImportedContacts, interactiveMsg = null, regularMsg = msg, section = "Phone")
        }
        binding.btnPhoneRegularCancel.setOnClickListener { binding.etPhoneRegularMsg.setText("") }

        // Clear per-section
        binding.btnClearCsv.setOnClickListener { viewModel.clearCsv() }
        binding.btnClearPhone.setOnClickListener { viewModel.clearPhone() }

        // Back button
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun openCsvPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "text/csv",
                "text/comma-separated-values",
                "application/csv",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            ))
        }
        csvPickerLauncher.launch(intent)
    }

    private fun importFromCsv(uri: Uri) {
        // Show simple progress while parsing
        val progress = AlertDialog.Builder(this)
            .setTitle(getString(R.string.import_contacts_title))
            .setMessage(getString(R.string.parsing_file))
            .setCancelable(false)
            .create()
        progress.show()

        lifecycleScope.launch {
            try {
                val parsed: List<Contact> = parseCsvOnIo(uri)
                viewModel.upsertCsv(parsed)
                Toast.makeText(this@ImportActivity, getString(R.string.toast_imported_csv, parsed.size), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ImportActivity, getString(R.string.toast_error_csv, e.message), Toast.LENGTH_SHORT).show()
            } finally { progress.dismiss() }
        }
    }

    private suspend fun parseCsvOnIo(uri: Uri): List<Contact> = withContext(Dispatchers.IO) {
        val out = LinkedHashMap<String, Contact>() // dedupe by phone
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                var isFirstLine = true
                while (reader.readLine().also { line = it } != null) {
                    if (isFirstLine) { isFirstLine = false; continue }
                    val parts = line!!.split(",")
                    if (parts.size >= 2) {
                        val name = parts[0].trim().replace("\"", "")
                        val phone = formatPhoneNumber(parts[1].trim().replace("\"", ""))
                        if (phone.isNotEmpty()) out[phone] = Contact(name, phone, ContactSource.CSV)
                    }
                }
            }
        }
        out.values.toList()
    }

    private fun checkContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) }

    // New: In-app multi-select phone contacts picker
    private fun openContactsMultiPicker() {
        val progress = AlertDialog.Builder(this)
            .setTitle(getString(R.string.import_contacts_title))
            .setMessage(getString(R.string.loading_contacts))
            .setCancelable(false)
            .create()
        progress.show()

        lifecycleScope.launch {
            val contacts = withContext(Dispatchers.IO) { fetchAllPhoneContacts() }
            progress.dismiss()

            if (contacts.isEmpty()) {
                Toast.makeText(this@ImportActivity, getString(R.string.no_contacts_found), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val names = contacts.map { "${it.name} (${it.phoneNumber})" }.toTypedArray()
            val selected = BooleanArray(contacts.size) { false }

            AlertDialog.Builder(this@ImportActivity)
                .setTitle(getString(R.string.select_contacts_title))
                .setMultiChoiceItems(names, selected) { _, which, isChecked -> selected[which] = isChecked }
                .setPositiveButton(getString(R.string.add_selected_button)) { _, _ ->
                    val picked = ArrayList<Contact>(contacts.size)
                    for (i in contacts.indices) if (selected[i]) picked.add(contacts[i])
                    viewModel.upsertPhone(picked)
                    Toast.makeText(this@ImportActivity, getString(R.string.added_n_contacts, picked.size), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(getString(R.string.cancel_button), null)
                .show()
        }
    }

    private fun fetchAllPhoneContacts(): List<Contact> {
        val out = LinkedHashMap<String, Contact>() // dedupe by phone
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val name = if (nameIdx >= 0) cursor.getString(nameIdx) ?: "Unknown" else "Unknown"
                val num = if (numIdx >= 0) cursor.getString(numIdx) ?: "" else ""
                val phone = formatPhoneNumber(num)
                if (phone.isNotEmpty()) out[phone] = Contact(name, phone, ContactSource.PHONE)
            }
        }
        return out.values.toList()
    }

    private fun confirmAndSend(contacts: List<Contact>, interactiveMsg: String?, regularMsg: String?, section: String) {
        val target = if (binding.cbSendToAll.isChecked) mergeAllContacts() else contacts
        if (target.isEmpty()) { Toast.makeText(this, getString(R.string.toast_no_contacts), Toast.LENGTH_SHORT).show(); return }
        val builder = StringBuilder(getString(R.string.confirm_send_message, target.size))
        if (binding.cbSendToAll.isChecked) builder.append("\nSection: All (CSV + Phone)") else builder.append("\nSection: ").append(section)
        interactiveMsg?.let { builder.append("\n\nInteractive Message:\n").append(it.take(80)).append(if (it.length > 80) "..." else "") }
        regularMsg?.let { builder.append("\n\nRegular Message:\n").append(it.take(80)).append(if (it.length > 80) "..." else "") }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_send_title))
            .setMessage(builder.toString())
            .setPositiveButton(getString(R.string.confirm_send_now)) { _, _ -> sendMessages(target, interactiveMsg, regularMsg) }
            .setNegativeButton(getString(R.string.cancel_button), null)
            .show()
    }

    private fun mergeAllContacts(): List<Contact> {
        val map = LinkedHashMap<String, Contact>()
        excelImportedContacts.forEach { map[it.phoneNumber] = it }
        phoneImportedContacts.forEach { map[it.phoneNumber] = it }
        return map.values.toList()
    }

    private fun sendMessages(contacts: List<Contact>, interactiveMsg: String?, regularMsg: String?) {
        val progressDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.sending_progress_title))
            .setMessage(getString(R.string.sending_progress_template, 0, contacts.size, 0, 0))
            .setCancelable(false)
            .create()
        progressDialog.show()

        lifecycleScope.launch(Dispatchers.IO) {
            var success = 0; var fail = 0; var sent = 0
            contacts.forEach { contact ->
                try {
                    var ok = true
                    if (!interactiveMsg.isNullOrBlank()) ok = ok && sendSmsMessage(contact.phoneNumber, interactiveMsg)
                    if (!regularMsg.isNullOrBlank()) ok = ok && sendSmsMessage(contact.phoneNumber, regularMsg)
                    sent++; if (ok) success++ else fail++
                } catch (_: Exception) { sent++; fail++ }
                withContext(Dispatchers.Main) { progressDialog.setMessage(getString(R.string.sending_progress_template, sent, contacts.size, success, fail)) }
            }
            withContext(Dispatchers.Main) {
                progressDialog.dismiss()
                showResultsDialog(success, fail)
            }
        }
    }

    private fun showResultsDialog(successCount: Int, failCount: Int) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.sending_complete_title))
            .setMessage(getString(R.string.sending_results_template, successCount, failCount, successCount + failCount))
            .setPositiveButton("OK", null)
            .show()
    }

    private suspend fun sendSmsMessage(phoneNumber: String, message: String): Boolean {
        return try {
            android.util.Log.d("SMS", "Sending to $phoneNumber: $message")
            delay(100)
            true
        } catch (e: Exception) {
            android.util.Log.e("SMS", "Failed to send", e)
            false
        }
    }

    private fun formatPhoneNumber(raw: String): String {
        val cleaned = raw.replace(Regex("[^0-9+]"), "")
        return when {
            cleaned.startsWith("+254") -> cleaned.removePrefix("+")
            cleaned.startsWith("254") -> cleaned
            cleaned.startsWith("0") && cleaned.length >= 10 -> "254" + cleaned.substring(1)
            cleaned.length == 9 -> "254$cleaned"
            else -> cleaned.filter { it.isDigit() }
        }
    }

    private fun updateContactCount() {
        binding.tvCsvCount.text = getString(R.string.csv_contacts_imported, excelImportedContacts.size)
        binding.tvPhoneCount.text = getString(R.string.phone_contacts_imported, phoneImportedContacts.size)
        binding.contactCountText.text = getString(R.string.contacts_imported, importedContacts.size)
    }

    private fun toggleSection(section: String) {
        when (section) {
            "csv" -> {
                val isVisible = binding.csvExpandableContent.visibility == android.view.View.VISIBLE
                binding.csvExpandableContent.visibility = if (isVisible) android.view.View.GONE else android.view.View.VISIBLE
                binding.phoneExpandableContent.visibility = android.view.View.GONE
            }
            "phone" -> {
                val isVisible = binding.phoneExpandableContent.visibility == android.view.View.VISIBLE
                binding.phoneExpandableContent.visibility = if (isVisible) android.view.View.GONE else android.view.View.VISIBLE
                binding.csvExpandableContent.visibility = android.view.View.GONE
            }
        }
    }
}
