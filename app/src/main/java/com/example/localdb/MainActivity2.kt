// MainActivity2.kt - Updated with BLE and Permissions
package com.example.localdb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.localdb.networking.BLEHelper
import com.example.localdb.networking.NetworkChecker
import com.example.localdb.networking.RemoteApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSave: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val networkChecker by lazy {
        NetworkChecker(getSystemService(ConnectivityManager::class.java))
    }

    private val LOCATION_REQUEST_CODE = 1001
    private val BLE_REQUEST_CODE = 1002

    private val bleHelper by lazy {
        (application as MyApp).bleHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)
        buttonSave = findViewById(R.id.btnLogin)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        buttonSave.setOnClickListener {
            val sharedPref = getSharedPreferences("Login Data", MODE_PRIVATE)
            val editor = sharedPref.edit()
            val n = editTextName.text.toString()
            val p = editTextPassword.text.toString()

            Log.d("MainActivity2", "Name: $n, Password: $p") // Debug Log

            if (n.isEmpty() || p.isEmpty()) {
                Log.e("MainActivity2", "Name or Password is empty!") // Error Log
                return@setOnClickListener
            }

            getLastKnownLocation { location ->
                if (location != null) {
                    Log.d("MainActivity2", "Konum: Lat=${location.latitude}, Lng=${location.longitude}")
                    Toast.makeText(
                        this,
                        "Konum: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("MainActivity2", "Konum alınamadı.")
                    Toast.makeText(this, "Konum alınamadı. Lütfen GPS'i kontrol edin.", Toast.LENGTH_SHORT).show()
                }
            }

            editor.putString("Name", n)
            editor.putString("Password", p)
            editor.apply()

            val userData = JSONObject().apply {
                put("username", n)
                put("email", "selin@example.com")
                put("password", p)
            }

            RemoteApi().register(userData) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "User created successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to create user.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            try {
                val i = Intent(this, MyTasksPage::class.java)
                startActivity(i)
                Log.d("MainActivity2", "Navigated to HomeScreen successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }

        checkAndRequestBLEPermissions()
    }

    private fun checkAndRequestBLEPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                BLE_REQUEST_CODE
            )
        } else {
            startBLEScanning()
        }
    }

    private fun startBLEScanning() {
        try {
            bleHelper.startScanning(bleHelper.exampleScanCallback)
        } catch (e: SecurityException) {
            Log.e("MainActivity2", "BLE scanning failed: ${e.message}")
        }
    }

    private fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            callback(null)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            callback(location)
        }.addOnFailureListener {
            Log.e("MainActivity2", "Konum alınamadı: ${it.localizedMessage}")
            callback(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastKnownLocation { location ->
                        if (location != null) {
                            Toast.makeText(
                                this,
                                "Konum: ${location.latitude}, ${location.longitude}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this, "Konum alınamadı.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show()
                }
            }
            BLE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    startBLEScanning()
                } else {
                    Toast.makeText(this, "BLE izinleri gerekli!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}