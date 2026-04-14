package com.example.assignment.model

import java.io.Serializable

data class User(
    val id: Int,
    val fullName: String,
    val username: String,
    val password: String,
    val roleId: Int,
    val roleName: String
) : Serializable
