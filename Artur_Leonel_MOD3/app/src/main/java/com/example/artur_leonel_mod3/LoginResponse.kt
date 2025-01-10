package com.example.artur_leonel_mod3

data class LoginResponse (
    val token: String,
    val email: String,
    val dr: String,
    val nome: String,
    val id: Int
)