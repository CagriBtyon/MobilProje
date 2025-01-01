package com.example.localdb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localdb.adapters.TaskAdapter
import com.example.localdb.broadcast.TaskCompletionReceiver
import com.example.localdb.models.Task
import com.example.localdb.networking.RemoteApi

class MyTasksPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tvTitle: TextView
    private lateinit var taskList: MutableList<Task>
    private lateinit var btnAddTask: Button

    // İzin isteme için launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_tasks_page)

        tvTitle = findViewById(R.id.tvTitle)
        recyclerView = findViewById(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btnAddTask = findViewById(R.id.btnAddTask)

        val sharedPref = getSharedPreferences("Login Data", MODE_PRIVATE)
        val username = sharedPref.getString("Name", "")

        checkAndRequestNotificationPermission()

        if (!username.isNullOrEmpty()) {
            tvTitle.text = "My Tasks - $username"
            loadTasksForUser(username)
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }

        // "Add Task" butonuna tıklandığında TaskPage'e yönlendir
        btnAddTask.setOnClickListener {
            val intent = Intent(this, TaskPage::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // İzin verilmemişse kullanıcıdan iste
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun loadTasksForUser(username: String) {
        RemoteApi().getUserTasks(username) { tasks ->
            runOnUiThread {
                if (tasks != null) {
                    taskList = tasks.filter { it.username == username }.toMutableList()

                    if (taskList.isNotEmpty()) {
                        taskAdapter = TaskAdapter(taskList) { updatedTask ->
                            onTaskUpdated(updatedTask)
                        }
                        recyclerView.adapter = taskAdapter
                    } else {
                        Toast.makeText(this, "No tasks found for $username.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to load tasks.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onTaskUpdated(task: Task) {
        val intent = Intent(this, TaskCompletionReceiver::class.java).apply {
            putExtra("taskName", task.taskname)
            putExtra("message", if (task.completed) "Task completed!" else "Task updated!")
        }
        sendBroadcast(intent)

        // RecyclerView öğesini güncelle
        val position = taskList.indexOf(task)
        if (position >= 0) {
            taskAdapter.notifyItemChanged(position)
        }
    }

}
