package com.example.movieapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState

    fun login(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Failure(task.exception?.message ?: "Login failed")
                    }
                }
        }
    }

    fun register(email: String, password: String) {
        _authState.value = AuthResult.Loading
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _authState.value = if (task.isSuccessful) {
                        AuthResult.Success
                    } else {
                        AuthResult.Failure(task.exception?.message ?: "Registration failed")
                    }
                }
        }
    }


    sealed class AuthResult {
        object Idle : AuthResult()
        object Loading : AuthResult()
        object Success : AuthResult()
        data class Failure(val error: String) : AuthResult()
    }
}
