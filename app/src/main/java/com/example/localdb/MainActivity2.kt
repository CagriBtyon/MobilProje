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

class MainActivity2 : AppCompatActivity() {

    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonSave: Button

    private val networkChecker by lazy {
        NetworkChecker(getSystemService(ConnectivityManager::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)
        buttonSave = findViewById(R.id.btnLogin)

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



            editor.putString("Name" , n)
            editor.putString("Password" , p)
            editor.apply()

            // JSON oluştur ve API'ye gönder
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

        RemoteApi().getFact()
    }
}
