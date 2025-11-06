package com.sandesh.voicecommand

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var transcriptText: TextView
    private lateinit var statusText: TextView
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    
    private val PERMISSION_REQUEST_CODE = 100
    private val CALL_PERMISSION_REQUEST_CODE = 101
    private var pendingCallContact: String? = null
    
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var isFlashlightOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        transcriptText = findViewById(R.id.transcriptText)
        statusText = findViewById(R.id.statusText)
        
        stopButton.isEnabled = false
        
        // Initialize camera manager for flashlight
        try {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager.cameraIdList[0]
        } catch (e: Exception) {
            android.util.Log.e("VoiceCommand", "Camera init error", e)
        }
        
        // Request all permissions at startup
        requestAllPermissions()
        
        startButton.setOnClickListener {
            if (checkPermission()) {
                startListening()
            } else {
                requestPermission()
            }
        }
        
        stopButton.setOnClickListener {
            stopListening()
        }
        
        // Debug: Long press status text to see installed apps
        statusText.setOnLongClickListener {
            showInstalledApps()
            true
        }
        
        initializeSpeechRecognizer()
    }
    
    private fun requestAllPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )
        
        // Add Bluetooth permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startListening()
                } else {
                    Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show()
                }
            }
            CALL_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pendingCallContact?.let { makeDirectCall(it) }
                    pendingCallContact = null
                } else {
                    Toast.makeText(this, "Call permission required to make calls", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    statusText.text = "🎤 Listening..."
                }
                
                override fun onBeginningOfSpeech() {
                    statusText.text = "🎤 Speaking..."
                }
                
                override fun onRmsChanged(rmsdB: Float) {}
                
                override fun onBufferReceived(buffer: ByteArray?) {}
                
                override fun onEndOfSpeech() {
                    statusText.text = "Processing..."
                }
                
                override fun onError(error: Int) {
                    statusText.text = "❌ Error: ${getErrorText(error)}"
                    stopListening()
                }
                
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        transcriptText.text = text
                        handleCommand(text)
                    }
                    stopListening()
                }
                
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        transcriptText.text = matches[0] + "..."
                    }
                }
                
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startListening() {
        // Recreate speech recognizer if needed
        if (speechRecognizer == null) {
            initializeSpeechRecognizer()
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        
        try {
            speechRecognizer?.startListening(intent)
            isListening = true
            startButton.isEnabled = false
            stopButton.isEnabled = true
            statusText.text = "🎤 Starting..."
            transcriptText.text = ""
        } catch (e: Exception) {
            statusText.text = "❌ Failed to start: ${e.message}"
            Toast.makeText(this, "Error starting speech recognition", Toast.LENGTH_SHORT).show()
            stopListening()
        }
    }
    
    private fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            android.util.Log.e("VoiceCommand", "Error stopping recognizer", e)
        }
        isListening = false
        startButton.isEnabled = true
        stopButton.isEnabled = false
        if (statusText.text == "🎤 Listening..." || statusText.text == "🎤 Speaking..." || statusText.text == "🎤 Starting...") {
            statusText.text = "Idle"
        }
    }
    
    private fun handleCommand(text: String) {
        val lowerText = text.lowercase()
        
        // Flashlight controls
        when {
            lowerText.contains("turn on") && (lowerText.contains("torch") || lowerText.contains("flashlight") || lowerText.contains("flash")) -> {
                toggleFlashlight(true)
                return
            }
            lowerText.contains("turn off") && (lowerText.contains("torch") || lowerText.contains("flashlight") || lowerText.contains("flash")) -> {
                toggleFlashlight(false)
                return
            }
        }
        
        // WiFi controls
        when {
            lowerText.contains("turn on") && lowerText.contains("wifi") -> {
                toggleWifi(true)
                return
            }
            lowerText.contains("turn off") && lowerText.contains("wifi") -> {
                toggleWifi(false)
                return
            }
        }
        
        // Bluetooth controls
        when {
            lowerText.contains("turn on") && lowerText.contains("bluetooth") -> {
                toggleBluetooth(true)
                return
            }
            lowerText.contains("turn off") && lowerText.contains("bluetooth") -> {
                toggleBluetooth(false)
                return
            }
        }
        
        // Volume controls
        when {
            lowerText.contains("volume up") || lowerText.contains("increase volume") -> {
                adjustVolume(true)
                return
            }
            lowerText.contains("volume down") || lowerText.contains("decrease volume") -> {
                adjustVolume(false)
                return
            }
            lowerText.contains("mute") -> {
                muteVolume()
                return
            }
        }
        
        // Pattern: "open <app>"
        if (lowerText.startsWith("open ")) {
            val appName = lowerText.removePrefix("open ").trim()
            openApp(appName)
            return
        }
        
        // Pattern: "play <song> on youtube/spotify"
        if (lowerText.startsWith("play ")) {
            val remainder = lowerText.removePrefix("play ").trim()
            when {
                remainder.contains(" on youtube") -> {
                    val query = remainder.replace(" on youtube", "").trim()
                    playOnYouTube(query)
                }
                remainder.contains(" on spotify") -> {
                    val query = remainder.replace(" on spotify", "").trim()
                    playOnSpotify(query)
                }
                else -> {
                    // Default to YouTube
                    playOnYouTube(remainder)
                }
            }
            return
        }
        
        // Pattern: "call <name>"
        if (lowerText.startsWith("call ")) {
            val contact = lowerText.removePrefix("call ").trim()
            openDialer(contact)
            return
        }
        
        // Pattern: "search for <query>" or "search <query>"
        if (lowerText.startsWith("search for ")) {
            val query = lowerText.removePrefix("search for ").trim()
            searchGoogle(query)
            return
        }
        
        if (lowerText.startsWith("search ")) {
            val query = lowerText.removePrefix("search ").trim()
            searchGoogle(query)
            return
        }
        
        // Pattern: "whatsapp <contact> <message>"
        if (lowerText.startsWith("whatsapp ")) {
            val remainder = lowerText.removePrefix("whatsapp ").trim()
            val parts = remainder.split(" ", limit = 2)
            if (parts.size >= 2) {
                val contact = parts[0]
                val message = parts[1]
                sendWhatsAppMessage(contact, message)
            } else {
                statusText.text = "❌ Say: whatsapp [name] [message]"
                Toast.makeText(this, "Format: whatsapp [contact name] [message]", Toast.LENGTH_LONG).show()
            }
            return
        }
        
        statusText.text = "✅ Recognized: $text"
    }
    
    private fun openApp(appName: String) {
        try {
            // Get all installed apps
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val allApps = packageManager.queryIntentActivities(intent, 0)
            
            // Search for app by name (case-insensitive)
            val searchName = appName.lowercase().trim()
            
            // Log all apps for debugging
            android.util.Log.d("VoiceCommand", "Searching for: $searchName")
            android.util.Log.d("VoiceCommand", "Total launcher apps: ${allApps.size}")
            
            val matchedApps = mutableListOf<String>()
            
            for (appInfo in allApps) {
                val appLabel = appInfo.loadLabel(packageManager).toString().lowercase()
                
                // Check if app name matches
                if (appLabel.contains(searchName) || searchName.contains(appLabel)) {
                    matchedApps.add(appLabel)
                    val packageName = appInfo.activityInfo.packageName
                    
                    android.util.Log.d("VoiceCommand", "Found match: $appLabel ($packageName)")
                    
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
                    
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(launchIntent)
                        statusText.text = "✅ Opened $appLabel"
                        Toast.makeText(this, "Opening $appLabel", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
            
            // App not found - show matched apps if any
            if (matchedApps.isNotEmpty()) {
                val matchList = matchedApps.joinToString(", ")
                statusText.text = "❌ Found but can't launch: $matchList"
                Toast.makeText(this, "Found apps: $matchList but couldn't launch them", Toast.LENGTH_LONG).show()
            } else {
                statusText.text = "❌ $appName not found"
                Toast.makeText(this, "$appName is not installed on this device", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            statusText.text = "❌ Error: ${e.message}"
            Toast.makeText(this, "Error opening app: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("VoiceCommand", "Error opening app", e)
        }
    }
    
    private fun getPackageName(appName: String): String? {
        return when (appName.lowercase()) {
            "youtube" -> "com.google.android.youtube"
            "gmail" -> "com.google.android.gm"
            "maps", "google maps" -> "com.google.android.apps.maps"
            "chrome", "google chrome" -> "com.google.android.chrome"
            "whatsapp" -> "com.whatsapp"
            "instagram" -> "com.instagram.android"
            "twitter", "x" -> "com.twitter.android"
            "facebook" -> "com.facebook.katana"
            "spotify" -> "com.spotify.music"
            "telegram" -> "org.telegram.messenger"
            "camera" -> "com.android.camera2"
            "gallery", "photos" -> "com.google.android.apps.photos"
            "calculator" -> "com.google.android.calculator"
            "clock" -> "com.google.android.deskclock"
            "calendar" -> "com.google.android.calendar"
            "contacts" -> "com.google.android.contacts"
            "phone", "dialer" -> "com.google.android.dialer"
            "messages", "messaging" -> "com.google.android.apps.messaging"
            "settings" -> "com.android.settings"
            "play store", "playstore" -> "com.android.vending"
            else -> null
        }
    }
    
    // Add helper method to list installed apps for debugging
    private fun debugListInstalledApps() {
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val googleApps = apps.filter { it.packageName.contains("google") || it.packageName.contains("youtube") }
        val appList = googleApps.joinToString("\n") { "${it.loadLabel(packageManager)}: ${it.packageName}" }
        android.util.Log.d("VoiceCommand", "Installed Google apps:\n$appList")
    }
    
    private fun showInstalledApps() {
        try {
            // Get all apps with launcher activities
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            val allApps = packageManager.queryIntentActivities(intent, 0)
            
            // Sort and format the list
            val appList = allApps
                .map { appInfo ->
                    val label = appInfo.loadLabel(packageManager).toString()
                    val packageName = appInfo.activityInfo.packageName
                    "$label\n($packageName)"
                }
                .sorted()
                .joinToString("\n\n")
            
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("All Launchable Apps (${allApps.size} apps)")
                .setMessage(appList)
                .setPositiveButton("OK", null)
                .show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openPlayStore(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("market://details?id=$packageName")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            }
            startActivity(intent)
        }
    }
    
    private fun openDialer(contact: String) {
        // Check if we have the necessary permissions
        val hasCallPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasContactsPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasCallPermission || !hasContactsPermission) {
            // Request permissions
            pendingCallContact = contact
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS),
                CALL_PERMISSION_REQUEST_CODE
            )
            return
        }
        
        makeDirectCall(contact)
    }
    
    private fun makeDirectCall(contactName: String) {
        try {
            // Search for the contact in the phonebook
            val phoneNumber = findContactPhoneNumber(contactName)
            
            if (phoneNumber != null) {
                // Make direct call
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
                statusText.text = "✅ Calling $contactName"
                Toast.makeText(this, "Calling $contactName at $phoneNumber", Toast.LENGTH_SHORT).show()
            } else {
                // Contact not found
                statusText.text = "❌ Contact $contactName not found"
                Toast.makeText(this, "No contact found with name: $contactName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            statusText.text = "❌ Call failed"
            Toast.makeText(this, "Failed to make call: ${e.message}", Toast.LENGTH_LONG).show()
            android.util.Log.e("VoiceCommand", "Call error", e)
        }
    }
    
    private fun findContactPhoneNumber(contactName: String): String? {
        var phoneNumber: String? = null
        val searchName = contactName.lowercase()
        
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            null
        )
        
        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(
                    it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                ).lowercase()
                
                // Check if the contact name matches
                if (name.contains(searchName) || searchName.contains(name)) {
                    phoneNumber = it.getString(
                        it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )
                    android.util.Log.d("VoiceCommand", "Found contact: $name -> $phoneNumber")
                    return phoneNumber // Return the first match
                }
            }
        }
        
        return phoneNumber
    }
    
    private fun searchGoogle(query: String) {
        try {
            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(android.app.SearchManager.QUERY, query)
            }
            startActivity(intent)
            statusText.text = "✅ Searching for: $query"
            Toast.makeText(this, "Searching Google for: $query", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to browser
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                }
                startActivity(browserIntent)
                statusText.text = "✅ Searching for: $query"
            } catch (e2: Exception) {
                statusText.text = "❌ Search failed"
                Toast.makeText(this, "Failed to search: ${e2.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun playOnYouTube(query: String) {
        try {
            val intent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", query)
            }
            startActivity(intent)
            statusText.text = "✅ Playing on YouTube: $query"
            Toast.makeText(this, "Searching YouTube for: $query", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Fallback to browser
            val browserIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
            }
            startActivity(browserIntent)
            statusText.text = "✅ Opening YouTube"
        }
    }
    
    private fun playOnSpotify(query: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("spotify:search:$query")
                setPackage("com.spotify.music")
            }
            startActivity(intent)
            statusText.text = "✅ Playing on Spotify: $query"
            Toast.makeText(this, "Searching Spotify for: $query", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Spotify not installed", Toast.LENGTH_SHORT).show()
            statusText.text = "❌ Spotify not found"
        }
    }
    
    private fun toggleFlashlight(turnOn: Boolean) {
        try {
            if (!::cameraManager.isInitialized) {
                cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
                cameraId = cameraManager.cameraIdList[0]
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId!!, turnOn)
                isFlashlightOn = turnOn
                statusText.text = if (turnOn) "✅ Flashlight ON" else "✅ Flashlight OFF"
                Toast.makeText(this, if (turnOn) "Flashlight turned ON" else "Flashlight turned OFF", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            statusText.text = "❌ Flashlight error"
            Toast.makeText(this, "Failed to toggle flashlight: ${e.message}", Toast.LENGTH_SHORT).show()
            android.util.Log.e("VoiceCommand", "Flashlight error", e)
        }
    }
    
    private fun toggleWifi(turnOn: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - open WiFi settings
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
                statusText.text = "Opening WiFi settings"
                Toast.makeText(this, "Please toggle WiFi manually", Toast.LENGTH_LONG).show()
            } else {
                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.isWifiEnabled = turnOn
                statusText.text = if (turnOn) "✅ WiFi ON" else "✅ WiFi OFF"
                Toast.makeText(this, if (turnOn) "WiFi turned ON" else "WiFi turned OFF", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            statusText.text = "❌ WiFi error"
            Toast.makeText(this, "Failed to toggle WiFi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun toggleBluetooth(turnOn: Boolean) {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
                return
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ - open Bluetooth settings
                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                startActivity(intent)
                statusText.text = "Opening Bluetooth settings"
                Toast.makeText(this, "Please toggle Bluetooth manually", Toast.LENGTH_LONG).show()
            } else {
                if (turnOn && !bluetoothAdapter.isEnabled) {
                    bluetoothAdapter.enable()
                } else if (!turnOn && bluetoothAdapter.isEnabled) {
                    bluetoothAdapter.disable()
                }
                statusText.text = if (turnOn) "✅ Bluetooth ON" else "✅ Bluetooth OFF"
                Toast.makeText(this, if (turnOn) "Bluetooth turned ON" else "Bluetooth turned OFF", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            statusText.text = "❌ Bluetooth error"
            Toast.makeText(this, "Failed to toggle Bluetooth: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun adjustVolume(increase: Boolean) {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (increase) {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
                )
                statusText.text = "✅ Volume increased"
            } else {
                audioManager.adjustStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI
                )
                statusText.text = "✅ Volume decreased"
            }
            Toast.makeText(this, if (increase) "Volume up" else "Volume down", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            statusText.text = "❌ Volume error"
            Toast.makeText(this, "Failed to adjust volume", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun muteVolume() {
        try {
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_MUTE,
                AudioManager.FLAG_SHOW_UI
            )
            statusText.text = "✅ Muted"
            Toast.makeText(this, "Volume muted", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            statusText.text = "❌ Mute error"
            Toast.makeText(this, "Failed to mute", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun sendWhatsAppMessage(contactName: String, message: String) {
        try {
            // Find contact phone number
            val phoneNumber = findContactPhoneNumber(contactName)
            
            if (phoneNumber != null) {
                // Clean phone number (remove spaces, dashes, etc.)
                val cleanNumber = phoneNumber.replace(Regex("[^0-9+]"), "")
                
                // Open WhatsApp with pre-filled message
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}")
                    setPackage("com.whatsapp")
                }
                
                startActivity(intent)
                statusText.text = "✅ Opening WhatsApp for $contactName"
                Toast.makeText(this, "Sending to $contactName: $message", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "❌ Contact $contactName not found"
                Toast.makeText(this, "Contact not found: $contactName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            statusText.text = "❌ WhatsApp failed"
            Toast.makeText(this, "WhatsApp error: ${e.message}\nMake sure WhatsApp is installed", Toast.LENGTH_LONG).show()
            android.util.Log.e("VoiceCommand", "WhatsApp error", e)
        }
    }
    
    private fun getErrorText(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}
