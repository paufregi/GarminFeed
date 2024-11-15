package paufregi.connectfeed.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.usecases.ClearCacheUseCase
import paufregi.connectfeed.core.usecases.GetSetupDoneUseCase
import paufregi.connectfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getSetupDoneUseCase: GetSetupDoneUseCase,
    val clearCacheUseCase: ClearCacheUseCase,
) : ViewModel() {

    val state = getSetupDoneUseCase().map { setupDone ->
        HomeState(setupDone)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeState())

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CleanCache -> clearCache()
        }
    }

    private fun clearCache() = viewModelScope.launch {
        clearCacheUseCase()
    }.invokeOnCompletion { cause ->
        viewModelScope.launch {
            when (cause == null) {
                true -> SnackbarController.sendEvent("Cache cleared")
                false -> SnackbarController.sendEvent("Unable to clear cache")
            }
        }
    }
}