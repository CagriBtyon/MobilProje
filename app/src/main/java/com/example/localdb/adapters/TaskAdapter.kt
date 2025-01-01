package com.example.localdb.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localdb.R
import com.example.localdb.models.Task

class TaskAdapter(
    private val tasks: List<Task>,
    private val onTaskUpdated: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.taskName)
        val explanation: TextView = itemView.findViewById(R.id.explanation)
        val completedCheckBox: CheckBox = itemView.findViewById(R.id.completedCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskName.text = task.taskname
        holder.explanation.text = task.explanation
        holder.completedCheckBox.isChecked = task.completed

        // Görev tamamlanma durumu değiştirildiğinde tetikleme
        holder.completedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            task.completed = isChecked
            onTaskUpdated(task)
        }
    }

    override fun getItemCount(): Int = tasks.size
}
