package com.example.mad_assignment1

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.*
import android.media.*
import android.net.Uri
import android.os.*
import android.telephony.SmsManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE = 100
    private var mediaPlayer: MediaPlayer? = null
    private var isSirenActive = false
    private lateinit var prefs: SharedPreferences
    private lateinit var tvContactInfo: TextView
    private lateinit var btnSiren: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences("safety_prefs", MODE_PRIVATE)
        tvContactInfo = findViewById(R.id.tvContactInfo)
        btnSiren = findViewById(R.id.btnSiren)

        displaySavedContacts()
        checkAndRequestPermissions()

        findViewById<Button>(R.id.btnSOS).setOnClickListener { handleSosTriggered() }
        btnSiren.setOnClickListener { if (isSirenActive) stopSiren() else playSiren() }
        findViewById<Button>(R.id.btnPolice).setOnClickListener { openDialer("112") }
        findViewById<Button>(R.id.btnAmbulance).setOnClickListener { openDialer("102") }
        findViewById<Button>(R.id.btnWomenHelpline).setOnClickListener { openDialer("1091") }
        findViewById<Button>(R.id.btnAddContact).setOnClickListener {
            startActivity(Intent(this, AddContactActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        displaySavedContacts()
    }

    private fun displaySavedContacts() {
        val phone = prefs.getString("contact_phone", "") ?: ""
        val name = prefs.getString("contact_name", "") ?: ""
        tvContactInfo.text = if (phone.isNotEmpty()) "Emergency Contact: $name ($phone)" else getString(R.string.no_contact)
    }

    private fun handleSosTriggered() {
        val phone = prefs.getString("contact_phone", "") ?: ""
        if (phone.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_contact), Toast.LENGTH_LONG).show()
            startActivity(Intent(this, AddContactActivity::class.java))
            return
        }

        playSiren()
        fetchLocationAndSendSms()

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.emergency_sos_title))
            .setMessage("SOS alarm is activated and SMS with your coordinates is being sent!\n\nDo you want to call ${prefs.getString("contact_name", "")} ($phone)?")
            .setPositiveButton("Call Now") { dialog, _ -> stopSiren(); openDialer(phone); dialog.dismiss() }
            .setNegativeButton("Stop Alarm") { dialog, _ -> stopSiren(); dialog.dismiss() }
            .setCancelable(false).show()
    }

    private fun playSiren() {
        stopSiren()
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)

            mediaPlayer = MediaPlayer.create(this, R.raw.alarm)
            if (mediaPlayer == null) {
                val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                mediaPlayer = MediaPlayer.create(this, alertUri)
            }

            mediaPlayer?.apply {
                isLooping = true
                start()
            }

            isSirenActive = true
            btnSiren.text = getString(R.string.stop_siren)
            btnSiren.setBackgroundColor(ContextCompat.getColor(this, R.color.red_sos))
        } catch (e: Exception) {
            Toast.makeText(this, "Could not start siren: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopSiren() {
        try {
            mediaPlayer?.let { if (it.isPlaying) it.stop(); it.release() }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            isSirenActive = false
            btnSiren.text = getString(R.string.start_siren)
            btnSiren.setBackgroundColor(ContextCompat.getColor(this, R.color.orange_siren))
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocationAndSendSms() {
        if (!hasPermissions()) return checkAndRequestPermissions()

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var loc: Location? = null
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (loc == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) { }

        val msg = if (loc != null) "EMERGENCY! I need immediate help. My location: https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
        else "EMERGENCY! I am in danger and need help immediately. Please contact me!"

        val phone = prefs.getString("contact_phone", "") ?: ""
        sendSms(phone, msg)
    }

    private fun sendSms(phone: String, message: String) {
        if (phone.isEmpty()) return
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) getSystemService(SmsManager::class.java) else SmsManager.getDefault()
            smsManager.sendMultipartTextMessage(phone, null, smsManager.divideMessage(message), null, null)
            Toast.makeText(this, "Emergency SMS sent to $phone", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS to $phone", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDialer(phone: String) {
        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
    }

    private fun hasPermissions() = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun checkAndRequestPermissions() {
        val list = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) list.add(Manifest.permission.SEND_SMS)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) list.add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) list.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) list.add(Manifest.permission.CALL_PHONE)
        if (list.isNotEmpty()) ActivityCompat.requestPermissions(this, list.toTypedArray(), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Please grant permissions for full functionality", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSiren()
    }
}