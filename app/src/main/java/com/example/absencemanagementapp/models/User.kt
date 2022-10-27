package com.example.absencemanagementapp.models

data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val cin: String,
    val cne: String,
    val filiere: String,
    val semester: String,
    val email: UserRole,
    val password: String
)
