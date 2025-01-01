// MainActivity2.kt - Updated with Debugging
package com.example.localdb
import android.widget.Toast

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log // Added for debugging
import com.example.localdb.networking.NetworkChecker
import com.example.localdb.networking.RemoteApi
import org.json.JSONObject

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity2 : AppCompatActivity() {

    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonSave: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val networkChecker by lazy {
        NetworkChecker(getSystemService(ConnectivityManager::class.java))
    }
    private val LOCATION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)
        buttonSave = findViewById(R.id.btnLogin)

        // FusedLocationProvider ba lat
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

            // Konum bilgisi al ve i le
            getLastKnownLocation { location ->
                if (location != null) {
                    Log.d("MainActivity2", "Konum: Lat=${location.latitude}, Lng=${location.longitude}")
                    Toast.makeText(
                        this,
                        "Konum: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.e("MainActivity2", "Konum al namad .")
                    Toast.makeText(this, "Konum al namad . L tfen GPS'i kontrol edin.", Toast.LENGTH_SHORT).show()
                }
            }

            editor.putString("Name" , n)
            editor.putString("Password" , p)
            editor.apply()

            // JSON olu tur ve API'ye g nder
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

        // Konum bilgisi al ve i le
        getLastKnownLocation { location ->
            if (location != null) {
                Log.d("MainActivity2", "Konum: Lat=${location.latitude}, Lng=${location.longitude}")
                Toast.makeText(
                    this,
                    "Konum: ${location.latitude}, ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Log.e("MainActivity2", "Konum al namad .")
                Toast.makeText(this, "Konum al namad . L tfen GPS'i kontrol edin.", Toast.LENGTH_SHORT).show()
            }
        }

        RemoteApi().getFact()
    }

    private fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //  zin yoksa izin iste
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            callback(null)
            return
        }

        // Son bilinen konumu al
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            callback(location)
        }.addOnFailureListener {
            Log.e("MainActivity2", "Konum al namad : ${it.localizedMessage}")
            callback(null)
        }
    }

    //  zin sonu lar n  i leme
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastKnownLocation { location ->
                if (location != null) {
                    Toast.makeText(
                        this,
                        "Konum: ${location.latitude}, ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Konum al namad .", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show()
        }
    }
}

