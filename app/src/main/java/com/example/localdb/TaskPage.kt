package com.example.localdb

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import org.json.JSONObject
import java.util.*

class TaskPage : AppCompatActivity() {

    private lateinit var editTextTaskName: EditText
    private lateinit var editTextExplanation: EditText
    private lateinit var checkBoxCompleted: CheckBox
    private lateinit var buttonDeadline: Button
    private lateinit var buttonSubmit: Button
    private lateinit var textViewDeadline: TextView

    // Firebase ve Google Sign-In için değişkenler
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_page)

        // Firebase Authentication başlat
        auth = FirebaseAuth.getInstance()

        // Google Sign-In yapılandırması
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Web istemci kimliği
            .requestEmail()
            .build()

        // Google Sign-In istemcisi oluştur
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // UI bileşenlerini bağla
        editTextTaskName = findViewById(R.id.editTextTaskName)
        editTextExplanation = findViewById(R.id.editTextExplanation)
        checkBoxCompleted = findViewById(R.id.checkBoxCompleted)
        buttonDeadline = findViewById(R.id.buttonDeadline)
        textViewDeadline = findViewById(R.id.textViewDeadline)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        // Deadline seçimi
        buttonDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDeadline = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                textViewDeadline.text = "Deadline: $selectedDeadline"
            }, year, month, day).show()
        }

        // Görev oluşturma
        buttonSubmit.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val explanation = editTextExplanation.text.toString()
            val completed = checkBoxCompleted.isChecked

            if (taskName.isEmpty()) {
                Toast.makeText(this, "Task name is required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskData = JSONObject().apply {
                put("username", "user")
                put("taskname", taskName)
                put("explanation", explanation)
                put("completed", completed)
                put("deadline", textViewDeadline.text.toString())
            }

            // Görev gönderimi mantığı buraya eklenebilir
            Toast.makeText(this, "Task submitted successfully!", Toast.LENGTH_SHORT).show()
        }

        // Google ile giriş yapma butonuna tıklama
        findViewById<Button>(R.id.buttonSignIn).setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Giriş Başarılı!", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    // Kullanıcı bilgileri: user?.displayName, user?.email
                } else {
                    Toast.makeText(this, "Giriş Başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
