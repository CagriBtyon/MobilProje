package com.example.localdb

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeScreen : AppCompatActivity() {

    lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_screen)

        resultTextView = findViewById(R.id.tvResult)

        val sharedPref = getSharedPreferences("Login Data" , MODE_PRIVATE)
        val name = sharedPref.getString("Name" , "")

        resultTextView.text = "Welcome : $name 😊"

    }
}