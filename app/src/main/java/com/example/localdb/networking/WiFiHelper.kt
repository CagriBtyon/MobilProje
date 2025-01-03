package com.example.localdb.networking
import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiInfo

class WiFiHelper(private val context: Context) {

    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Check if WiFi is enabled
    fun isWiFiEnabled(): Boolean {
        return wifiManager.isWifiEnabled
    }

    // Enable WiFi
    fun enableWiFi(): Boolean {
        return if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
            true
        } else {
            false
        }
    }

    // Get connected WiFi network info
    fun getConnectedWiFiInfo(): WifiInfo? {
        return if (wifiManager.connectionInfo != null && isWiFiEnabled()) {
            wifiManager.connectionInfo
        } else {
            null
        }
    }

    // Disable WiFi
    fun disableWiFi(): Boolean {
        return if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
            true
        } else {
            false
        }
    }
}

