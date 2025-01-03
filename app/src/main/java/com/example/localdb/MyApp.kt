package com.example.localdb

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.localdb.networking.WiFiHelper
import com.example.localdb.networking.BLEHelper

class MyApp : Application() {
    lateinit var bleHelper: BLEHelper
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeWiFi()
        initializeBLE()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for task updates"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun initializeWiFi() {
        val wifiHelper = WiFiHelper(this)
        if (!wifiHelper.isWiFiEnabled()) {
            wifiHelper.enableWiFi()
            println("WiFi etkinleştirildi!")
        } else {
            println("WiFi zaten etkin!")
        }
    }
    private fun initializeBLE() {
        bleHelper = BLEHelper(this)
        bleHelper.startScanning(bleHelper.exampleScanCallback)
    }
}