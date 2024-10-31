package paufregi.garminfeed.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.core.usecases.SaveCredentialUseCase
import paufregi.garminfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCredentialUseCase: GetCredentialUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())

    val state = _state
        .onStart { loadCredential() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SettingsState())

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateUsername -> _state.update { it.copy(credential = it.credential.copy(username = event.username)) }
            is SettingsEvent.UpdatePassword -> _state.update { it.copy(credential = it.credential.copy(password = event.password)) }
            is SettingsEvent.UpdateShowPassword -> _state.update { it.copy(showPassword = event.showPassword) }
            is SettingsEvent.SaveCredential -> saveCredential(event.callback)
        }
    }

    private fun loadCredential() = viewModelScope.launch {
        getCredentialUseCase().collect { cred ->
            _state.update { it.copy(credential = cred ?: it.credential) } }
        }

    private fun saveCredential(callback: () -> Unit) = viewModelScope.launch {
        when (saveCredentialUseCase(state.value.credential) ) {
            is Result.Success -> SnackbarController.sendEvent("Credential saved")
            is Result.Failure -> SnackbarController.sendEvent("Unable to save credential")
        }
        callback()
    }
}