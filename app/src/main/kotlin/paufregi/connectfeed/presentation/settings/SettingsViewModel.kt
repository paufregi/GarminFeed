package paufregi.connectfeed.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetCredential
import paufregi.connectfeed.core.usecases.SaveCredential
import paufregi.connectfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCredential: GetCredential,
    private val saveCredential: SaveCredential,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())

    val state = _state
        .onStart { loadCredential() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SettingsState())

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.SetUsername -> _state.update { it.copy(credential = it.credential.copy(username = event.username)) }
            is SettingsEvent.SetPassword -> _state.update { it.copy(credential = it.credential.copy(password = event.password)) }
            is SettingsEvent.SetShowPassword -> _state.update { it.copy(showPassword = event.showPassword) }
            is SettingsEvent.Save -> save()
        }
    }

    private fun loadCredential() = viewModelScope.launch {
        getCredential().collect { cred ->
            _state.update { it.copy(credential = cred ?: it.credential) } }
        }

    private fun save() = viewModelScope.launch {
        when (saveCredential(state.value.credential) ) {
            is Result.Success -> SnackbarController.sendEvent("Credential saved")
            is Result.Failure -> SnackbarController.sendEvent("Unable to save credential")
        }
    }
}