package com.example.batterystatusmonitor

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class BatteryActivity : ComponentActivity() {
    // UI elements
    private lateinit var btnGetBattery: Button
    private lateinit var batteryLevel: TextView
    private lateinit var chargingStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_battery)    // Load our screen layout

        // Connect UI views with IDs from the XML
        btnGetBattery = findViewById(R.id.btnGetBattery)
        batteryLevel = findViewById(R.id.batteryLevel)
        chargingStatus = findViewById(R.id.chargingStatus)

        // When button is clicked → query native battery info and update UI
        btnGetBattery.setOnClickListener {
            val (levelText, statusText) = getBatteryInfo()
            batteryLevel.text = levelText
            chargingStatus.text = statusText
        }
    }

    // Gets battery percentage and charging status.Uses BatteryManager when available, otherwise falls back to broadcast data.
    private fun getBatteryInfo(): Pair<String, String> {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        // Check if the device supports the modern BatteryManager API
        val isModernApi = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

        val levelPercent: Int

        if (isModernApi) {
            // Try to get battery percentage using the direct modern API
            val cap = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            // If the value is valid (>= 0), use it
            if (cap >= 0) {
                levelPercent = cap
            } else {
                // If invalid (-1), fall back to reading from battery intent
                levelPercent = getBatteryLevelFromIntent()
            }
        } else {
            // Older Android versions MUST use the fallback method
            levelPercent = getBatteryLevelFromIntent()
        }

        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        // CHARGING / DISCHARGING / FULL / UNKNOWN
        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        // Physical connection type: 0 = unplugged, >0 = plugged into USB/AC/Wireless
        val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0

        // Treat the device as charging if it is actually plugged in or if Android reports charging
        val isCharging = plugged != 0 ||
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val levelText = "Battery: ${levelPercent}%"
        val statusText = "Status: ${if (isCharging) "Charging" else "Discharging"}"

        return Pair(levelText, statusText)
    }

    // Fallback method to calculate battery percentage from the ACTION_BATTERY_CHANGED intent.
    private fun getBatteryLevelFromIntent(): Int {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1

        // Convert level + scale to percentage if valid
        return if (level >= 0 && scale > 0) {
            (level * 100) / scale
        } else {
            -1 // Unknown
        }
    }
}
