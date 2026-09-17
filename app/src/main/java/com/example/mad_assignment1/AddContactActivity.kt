package com.example.mad_assignment1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val tvCurrentContact = findViewById<TextView>(R.id.tvCurrentContact)

        val prefs = getSharedPreferences("safety_prefs", MODE_PRIVATE)

        // Load existing saved contact
        val savedName = prefs.getString("contact_name", "") ?: ""
        val savedPhone = prefs.getString("contact_phone", "") ?: ""

        if (savedPhone.isNotEmpty()) {
            etName.setText(savedName)
            etPhone.setText(savedPhone)
        }

        updateStatusText(tvCurrentContact, savedName, savedPhone)

        // Save button listener
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Please enter contact name"
                etName.requestFocus()
                return@setOnClickListener
            }

            if (phone.isEmpty() || phone.length < 5) {
                etPhone.error = "Please enter a valid phone number"
                etPhone.requestFocus()
                return@setOnClickListener
            }

            val editor = prefs.edit()
            editor.putString("contact_name", name)
            editor.putString("contact_phone", phone)
            editor.apply()

            Toast.makeText(this, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show()
            finish()
        }

        // Clear contact listener
        btnClear.setOnClickListener {
            prefs.edit().clear().apply()
            etName.text.clear()
            etPhone.text.clear()
            updateStatusText(tvCurrentContact, "", "")
            Toast.makeText(this, "Emergency contact cleared", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateStatusText(textView: TextView, name: String, phone: String) {
        textView.text = if (phone.isNotEmpty()) "Contact: $name ($phone)" else "No contact saved."
    }
}