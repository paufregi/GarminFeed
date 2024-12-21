package paufregi.connectfeed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.usecases.IsLoggedIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isLoggedIn: IsLoggedIn
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())

    val state = _state
        .onStart { load() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), MainState())

    private fun load() = viewModelScope.launch {
        isLoggedIn().map { loggedIn -> _state.update { it.copy(loggedIn = loggedIn) } }
    }

    fun showLogin() = _state.update { it.copy(showLogin = true) }
    fun hideLogin() = _state.update { it.copy(showLogin = false) }
}