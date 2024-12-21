package paufregi.connectfeed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import paufregi.connectfeed.core.usecases.IsLoggedIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    isLoggedIn: IsLoggedIn
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())

    val state = combine(_state, isLoggedIn()) { state, logged -> state.copy(loggedIn = logged) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), MainState())

    fun showLogin() = _state.update { it.copy(showLogin = true) }
    fun hideLogin() = _state.update { it.copy(showLogin = false) }
}