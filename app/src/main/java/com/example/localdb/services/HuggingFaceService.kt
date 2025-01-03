package com.example.localdb.services

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import android.util.Log

object HuggingFaceService {

    private const val API_URL = "https://api-inference.huggingface.co/models/distilbert-base-uncased-finetuned-sst-2-english"
    private const val API_KEY = "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"

    private val client by lazy { OkHttpClient() } // Tek seferlik istemci oluşturma

    fun analyzeSentiment(taskDescription: String): String {
        val jsonInput = JSONObject().apply {
            put("inputs", taskDescription)
        }

        val requestBody = RequestBody.create(
            "application/json".toMediaType(),
            jsonInput.toString()
        )

        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $API_KEY")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                Log.e("API_ERROR", "Code: ${response.code}, Error: $errorBody")
                throw Exception("Hugging Face API çağrısı başarısız: ${response.code}")
            }

            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty()) {
                throw Exception("Hugging Face API'den boş yanıt döndü")
            }

            // Yanıtı işleme
            val jsonArray = JSONArray(responseBody)
            val label = jsonArray.getJSONObject(0).getString("label")

            return when (label) {
                "POSITIVE" -> "Bu görev pozitif bir hisle yazılmış."
                "NEGATIVE" -> "Bu görev negatif bir hisle yazılmış."
                else -> "Bu görev nötr bir his içeriyor."
            }
        }
    }
}
