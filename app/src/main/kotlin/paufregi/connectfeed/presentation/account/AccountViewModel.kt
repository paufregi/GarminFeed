package paufregi.connectfeed.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.RefreshToken
import paufregi.connectfeed.core.usecases.SignOut
import paufregi.connectfeed.presentation.ui.components.ProcessState
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    val signOutUseCase: SignOut,
    val refreshTokenUseCase: RefreshToken,
) : ViewModel() {

    private val _state = MutableStateFlow(AccountState())

    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), AccountState())

    fun onEvent(event: AccountEvent) = when (event) {
        is AccountEvent.ChangePassword -> viewModelScope.launch { refreshToken() }
        is AccountEvent.RefreshTokens -> viewModelScope.launch { refreshToken() }
        is AccountEvent.SignOut -> viewModelScope.launch { signOut() }
        is AccountEvent.Reset -> viewModelScope.launch { _state.update { AccountState(ProcessState.Idle) } }
    }

    private fun signOut() = viewModelScope.launch {
        _state.update { AccountState(ProcessState.Processing) }
        signOutUseCase()
        _state.update { AccountState(ProcessState.Success("Signed out")) }
    }

    private fun refreshToken() = viewModelScope.launch {
        _state.update { AccountState(ProcessState.Processing) }
        when (val res = refreshTokenUseCase()) {
            is Result.Failure -> _state.update { AccountState(ProcessState.Failure(res.reason)) }
            is Result.Success -> _state.update { AccountState(ProcessState.Success("Token refreshed")) }
        }
    }
}