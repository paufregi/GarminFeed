package paufregi.connectfeed.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetLatestActivities
import paufregi.connectfeed.core.usecases.GetProfiles
import paufregi.connectfeed.core.usecases.RefreshToken
import paufregi.connectfeed.core.usecases.SignOut
import paufregi.connectfeed.core.usecases.UpdateActivity
import paufregi.connectfeed.presentation.profile.EditProfileState
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
    }

    private fun signOut() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        signOutUseCase()
        _state.update { it.copy(processing = ProcessState.Success) }
    }

    private fun refreshToken() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        refreshTokenUseCase()
        _state.update { it.copy(processing = ProcessState.Success) }
    }
}