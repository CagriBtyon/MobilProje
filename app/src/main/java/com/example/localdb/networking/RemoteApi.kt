package com.example.localdb.networking

import android.util.Log
import androidx.compose.ui.graphics.colorspace.Connector
import com.example.localdb.AppConstants
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RemoteApi {
    val BASE_URL = "https://api.api-ninjas.com/v1/facts"

    val TAG = "Remote API"

    fun getFact(){
        Thread({
            var connection = URL(BASE_URL).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type" , "application/json")
            connection.setRequestProperty("Accept" , "application/json")
            connection.setRequestProperty("X-Api-Key" , AppConstants.API_KEY)
            connection.connectTimeout = 10000
            connection.readTimeout    = 10000
            connection.doInput = true

            try {
                val reader = InputStreamReader(connection.inputStream) // connection.inputStream, sunucudan dönen yanıtın içeriğini okur.

                reader.use { input ->
                    val response = StringBuilder()
                    val bufferedReader = BufferedReader(input)

                    bufferedReader.forEachLine {
                        response.append(it.trim())
                    }
                    Log.d(TAG , "In Success ${response.toString()}")
                }
            }
            catch (e: Exception){
                Log.d(TAG , "In Error ${e.localizedMessage}")

            }

            connection.disconnect()
        }).start()
    }

}
