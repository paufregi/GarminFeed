package paufregi.connectfeed.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.SetupDone
import paufregi.connectfeed.core.usecases.SignIn
import paufregi.connectfeed.presentation.ui.components.ProcessState
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val signInUseCase: SignIn,
    private val setupDone: SetupDone,
) : ViewModel() {

    private val _state = MutableStateFlow(SetupState())

    val state = _state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), SetupState())

    fun onEvent(event: SetupEvent) {
        when (event) {
            is SetupEvent.SetUsername -> _state.change(username = event.username)
            is SetupEvent.SetPassword -> _state.change(password = event.password)
            is SetupEvent.ShowPassword -> _state.change(showPassword = event.showPassword)
            is SetupEvent.Reset -> _state.update { SetupState() }
            is SetupEvent.SignIn -> signIn()
            is SetupEvent.Done -> done()
        }
    }

    private fun signIn() = viewModelScope.launch {
        _state.change(processState = ProcessState.Processing)
        when (val res = signInUseCase(state.value.credential) ) {
            is Result.Success -> _state.change(processState = ProcessState.Success(res.data))
            is Result.Failure -> _state.change(processState = ProcessState.Failure(res.reason))
        }
    }

    private fun done() = viewModelScope.launch {
        setupDone()
    }
}