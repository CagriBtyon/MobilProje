package com.example.localdb.models

data class Task(
    val username: String,
    val taskname: String,
    val explanation: String,
    val completed: Boolean,
    val deadline:String
)