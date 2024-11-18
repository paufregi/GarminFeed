package paufregi.connectfeed.presentation.syncweight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.SyncWeightUseCase
import paufregi.connectfeed.presentation.utils.ProcessState
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class SyncWeightModelView @Inject constructor(
    val syncWeightUseCase: SyncWeightUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SyncWeightState>(SyncWeightState())
    val state = _state.asStateFlow()

    fun syncWeight(inputStream: InputStream?) = viewModelScope.launch {
        _state.update { it.copy(loading = ProcessState.Processing) }
        if (inputStream == null) {
            _state.update { it.copy(loading = ProcessState.Failure) }
            return@launch
        }
        when (syncWeightUseCase(inputStream)) {
            is Result.Success -> _state.update { it.copy(loading = ProcessState.Success) }
            is Result.Failure -> _state.update { it.copy(loading = ProcessState.Failure) }
        }
    }
}