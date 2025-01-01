package com.example.localdb

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localdb.adapters.TaskAdapter
import com.example.localdb.models.Task
import com.example.localdb.networking.RemoteApi
import com.example.localdb.R

class MyTasksPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks_page)

        // Başlık TextView'i bağla
        tvTitle = findViewById(R.id.tvTitle)

        // RecyclerView'i bağla
        recyclerView = findViewById(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Kullanıcı adıyla görevleri yükle
        val sharedPref = getSharedPreferences("Login Data", MODE_PRIVATE)
        val username = sharedPref.getString("Name", "")

        if (!username.isNullOrEmpty()) {
            // Başlığa kullanıcı adını ekle
            tvTitle.text = "My Tasks - $username"
            loadTasksForUser(username)
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTasksForUser(username: String) {
        RemoteApi().getUserTasks(username) { tasks ->
            runOnUiThread {
                if (tasks != null) {
                    // Kullanıcı adına göre filtreleme
                    val filteredTasks = tasks.filter { it.username == username }

                    if (filteredTasks.isNotEmpty()) {
                        // Filtrelenmiş görevleri RecyclerView'e bağla
                        taskAdapter = TaskAdapter(filteredTasks)
                        recyclerView.adapter = taskAdapter
                    } else {
                        // Eğer kullanıcıya ait görev yoksa
                        Toast.makeText(this, "No tasks found for $username.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to load tasks.", Toast.LENGTH_SHORT).show()
                }
            }
            }
        }
}