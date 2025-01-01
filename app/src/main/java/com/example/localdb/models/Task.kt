package com.example.localdb.models

data class Task(
    val username: String,
    val taskname: String,
    val explanation: String,
    var completed: Boolean,
    val deadline:String
)