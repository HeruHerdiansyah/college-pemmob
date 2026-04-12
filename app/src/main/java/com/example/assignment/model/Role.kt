package com.example.assignment.model

import java.io.Serializable

data class Role(
    val id: Int,
    val roleName: String,
    val description: String
) : Serializable {
    override fun toString(): String = roleName
}
