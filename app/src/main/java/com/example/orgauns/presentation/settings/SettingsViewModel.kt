package com.example.orgauns.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orgauns.data.repository.AuthRepositoryImpl
import com.example.orgauns.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val userEmail: String = ""
)

class SettingsViewModel(
    context: Context,
    private val settingsRepository: SettingsRepositoryImpl = SettingsRepositoryImpl(context),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkMode,
                authRepository.currentUser
            ) { isDarkMode, user ->
                SettingsUiState(
                    isDarkMode = isDarkMode,
                    userEmail = user?.email ?: ""
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!_uiState.value.isDarkMode)
        }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onLogout()
        }
    }
}

