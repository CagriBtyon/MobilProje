package com.example.localdb.networking
import com.example.localdb.models.Task
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.ui.graphics.colorspace.Connector
import com.example.localdb.AppConstants
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RemoteApi {
    val BASE_URL = "https://api.api-ninjas.com/v1/facts"

    val TAG = "Remote API"

    fun getFact() {
        Thread({
            var connection = URL(BASE_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("X-Api-Key", AppConstants.API_KEY)
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.doInput = true

            try {
                val reader =
                    InputStreamReader(connection.inputStream) // connection.inputStream, sunucudan dönen yanıtın içeriğini okur.

                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)

                    bufferedReader.forEachLine {
                        response.append(it.trim())
                    }
                    Log.d(TAG, "In Success ${response.toString()}")
                }
            } catch (e: Exception) {
                Log.d(TAG, "In Error ${e.localizedMessage}")

            }

            connection.disconnect()
        }).start()
    }

    val BASE_URL2 =
        "mongodb+srv://mobil:mobil2023*@taskappdb.vq4bp.mongodb.net/?retryWrites=true&w=majority&appName=taskappDB"
    val BASE_URL3 = "http://10.0.2.2:8080"
    fun createTask(taskData: JSONObject, callback: (Boolean) -> Unit) {
        Thread {
            try {
                val url = URL("$BASE_URL3/createTask")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                connection.outputStream.use { it.write(taskData.toString().toByteArray()) }
                Log.d(TAG, "Request Body: $taskData")

                val responseCode = connection.responseCode
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d(TAG, "Response Code: $responseCode, Response Body: $response")

                callback(responseCode == HttpURLConnection.HTTP_OK)
            } catch (e: Exception) {
                Log.e(TAG, "Error in createTask: ${e.message}")
                callback(false)
            }
        }.start()
    }

    fun register(userData: JSONObject, callback: (Boolean) -> Unit) {
        Thread {
            try {
                Log.d(TAG, "Starting register function...") // Başlangıç logu

                val url = URL("$BASE_URL3/register") // Kullanıcı oluşturma endpoint'i
                Log.d(TAG, "URL: $url") // URL kontrol logu

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.doOutput = true

                Log.d(TAG, "Request Properties Set. Preparing to send data...") // Header logu

                // JSON verisini gönderiyoruz
                connection.outputStream.use {
                    it.write(userData.toString().toByteArray())
                    Log.d(TAG, "Request Body Sent: $userData") // Gönderilen veri logu
                }

                val responseCode = connection.responseCode
                Log.d(TAG, "Response Code: $responseCode") // Yanıt kodu logu

                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No response body"
                }
                Log.d(TAG, "Response Body: $response") // Yanıt gövdesi logu

                callback(responseCode == HttpURLConnection.HTTP_OK)
            } catch (e: Exception) {
                Log.e(TAG, "Error in register: ${e.message}", e) // Hata logu
                callback(false)
            }
        }.start()
    }

    fun getUserTasks(username: String, callback: (List<Task>?) -> Unit) {
        Thread {
            try {

                //val url = URL("$BASE_URL3/getTasksByUsername?username=$username")
                val url = URL("$BASE_URL3/getAllTasks")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000


                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    val tasks = parseTasks(response) // JSON'dan Task listesine çevirme
                    callback(tasks)
                } else {
                    callback(null)
                }
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }.start()
    }

    private fun parseTasks(response: String): List<Task> {
        val tasks = mutableListOf<Task>()
        val jsonArray = JSONArray(response)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            tasks.add(
                Task(
                    username = jsonObject.getString("username"),
                    taskname = jsonObject.getString("taskname"),
                    explanation = jsonObject.getString("explanation"),
                    completed = jsonObject.getBoolean("completed"),
                    deadline = jsonObject.getString("deadline")
                )
            )
        }
        return tasks
        }

}