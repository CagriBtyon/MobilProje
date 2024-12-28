package com.example.localdb

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity2 : AppCompatActivity() {

    lateinit var editTextName: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonSave: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextName = findViewById(R.id.eTName)
        editTextPassword = findViewById(R.id.eTPassword)
        buttonSave = findViewById(R.id.btnSave)

        buttonSave.setOnClickListener {
            var sharedPref = getSharedPreferences("Login Data" , MODE_PRIVATE)
            val editor = sharedPref.edit()
            val n = editTextName.text.toString()
            val p = editTextPassword.text.toString()
            editor.putString("Name" , n)
            editor.putString("Password" , p)
            editor.apply()

            val i = Intent(this , HomeScreen::class.java)
            startActivity(i)
        }
    }
}