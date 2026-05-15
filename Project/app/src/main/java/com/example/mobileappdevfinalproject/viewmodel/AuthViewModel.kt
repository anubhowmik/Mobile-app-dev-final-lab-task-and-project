package com.example.mobileappdevfinalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileappdevfinalproject.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

/**
 * ViewModel sits between the Activity (UI) and the Repository (data/Firebase).
 * It survives screen rotations — the Activity does not.
 * Activities observe LiveData here and react to changes.
 */
class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // --- Loading state ---
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // --- Login result: Success = FirebaseUser, Failure = Exception ---
    private val _loginResult = MutableLiveData<Result<FirebaseUser>>()
    val loginResult: LiveData<Result<FirebaseUser>> = _loginResult

    // --- Register result ---
    private val _registerResult = MutableLiveData<Result<FirebaseUser>>()
    val registerResult: LiveData<Result<FirebaseUser>> = _registerResult

    // --- Password reset result ---
    private val _passwordResetResult = MutableLiveData<Result<Unit>>()
    val passwordResetResult: LiveData<Result<Unit>> = _passwordResetResult

    fun loginUser(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginUser(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }

    fun registerUser(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password)
            _registerResult.value = result
            _isLoading.value = false
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            val result = authRepository.sendPasswordReset(email)
            _passwordResetResult.value = result
        }
    }
}
