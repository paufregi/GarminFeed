package paufregi.garminfeed.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val getCredentialUseCase: GetCredentialUseCase,
    val clearCacheUseCase: ClearCacheUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state
        .onStart { checkSetup() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeState())

    private fun checkSetup() = viewModelScope.launch {
        val cred = getCredentialUseCase()
        _state.update { it.copy(setupDone = cred != null && cred.username.isNotBlank() && cred.password.isNotBlank()) }
    }

    fun clearCache() = viewModelScope.launch {
        clearCacheUseCase()
    }.invokeOnCompletion { cause ->
        when (cause == null) {
            true -> _state.update { it.copy(clearCacheMessage = "Cache cleared") }
            false -> _state.update { it.copy(clearCacheMessage = "Unable to clear cache") }
        }
    }

    fun resetClearCacheMessage() = _state.update { it.copy(clearCacheMessage = null) }
}