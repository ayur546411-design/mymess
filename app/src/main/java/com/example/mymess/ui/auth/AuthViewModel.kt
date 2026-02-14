package com.example.mymess.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)

    fun login(onSuccess: (Boolean) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            loginError = "Please enter both username and password"
            return
        }

        // Fake Authentication Logic
        // Admin: admin / admin
        // User: user / user
        if (username == "1" && password == "1") {
            loginError = null
            onSuccess(true) // true = isAdmin
        } else if (username == "user" && password == "user") {
            loginError = null
            onSuccess(false) // false = isUser
        } else {
            loginError = "Invalid credentials"
        }
    }
}
