package com.example.localdb

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.localdb.networking.RemoteApi
import com.example.localdb.services.HuggingFaceService
import org.json.JSONObject
import java.util.*

class TaskPage : AppCompatActivity() {

    private lateinit var editTextTaskName: EditText
    private lateinit var editTextExplanation: EditText
    private lateinit var checkBoxCompleted: CheckBox
    private lateinit var buttonDeadline: Button
    private lateinit var buttonSubmit: Button
    private lateinit var textViewDeadline: TextView
    private lateinit var buttonAnalyze: Button // Duygusal analiz butonu
    private lateinit var textViewAnalysisResult: TextView // Analiz sonucu göstermek için

    private var selectedDeadline: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_page)

        // Arayüz bileşenlerini bağla
        editTextTaskName = findViewById(R.id.editTextTaskName)
        editTextExplanation = findViewById(R.id.editTextExplanation)
        checkBoxCompleted = findViewById(R.id.checkBoxCompleted)
        buttonDeadline = findViewById(R.id.buttonDeadline)
        textViewDeadline = findViewById(R.id.textViewDeadline)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonAnalyze = findViewById(R.id.buttonAnalyze) // Analiz butonunu bağla
        textViewAnalysisResult = findViewById(R.id.textViewAnalysisResult) // Analiz sonucu TextView'i bağla

        // Deadline seçimi için takvim
        buttonDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedDeadline = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                textViewDeadline.text = "Deadline: $selectedDeadline"
            }, year, month, day).show()
        }

        // Görevi API'ye gönder
        buttonSubmit.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val explanation = editTextExplanation.text.toString()
            val completed = checkBoxCompleted.isChecked

            if (taskName.isEmpty() || selectedDeadline.isEmpty()) {
                Toast.makeText(this, "Task name and deadline are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val taskData = JSONObject().apply {
                put("username", "veri")
                put("taskname", taskName)
                put("explanation", explanation)
                put("completed", completed)
                put("deadline", selectedDeadline)
            }

            RemoteApi().createTask(taskData) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show()
                        clearFields()
                    } else {
                        Toast.makeText(this, "Failed to create task.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    // Görev açıklamasını analiz et
    buttonAnalyze.setOnClickListener {
        val explanation = editTextExplanation.text.toString().trim()

        if (explanation.isEmpty()) {
            Toast.makeText(this, "Explanation is required for analysis!", Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        try {
            val analysisResult = HuggingFaceService.analyzeSentiment(explanation)
            textViewAnalysisResult.text = analysisResult
        } catch (e: Exception) {
            Toast.makeText(this, "Analysis failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
    // Alanları temizle
    private fun clearFields() {
        editTextTaskName.text.clear()
        editTextExplanation.text.clear()
        checkBoxCompleted.isChecked = false
        textViewDeadline.text = ""
        selectedDeadline=""
        }
}