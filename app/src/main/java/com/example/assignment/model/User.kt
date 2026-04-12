package com.example.assignment.model

import java.io.Serializable

data class User(
    val id: Int,
    val username: String,
    val password: String,
    val fullName: String,
    val role: Role
) : Serializable
