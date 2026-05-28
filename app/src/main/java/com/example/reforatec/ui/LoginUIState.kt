package com.example.reforatec.ui

data class LoginUIState (
    val correoInput: String = "",
    val passwordInput: String = "",
    val correoError: String? = null,
    val passwordError: String? = null
)