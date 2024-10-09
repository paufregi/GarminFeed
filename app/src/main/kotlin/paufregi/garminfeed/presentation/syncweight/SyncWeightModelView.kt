package paufregi.garminfeed.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.SyncWeightUseCase
import paufregi.garminfeed.presentation.syncweight.SyncWeightState
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class SyncWeightModelView @Inject constructor(
    private val syncWeightUseCase: SyncWeightUseCase
) : ViewModel() {

    private val mutableStatus = mutableStateOf<SyncWeightState?>(null)
    val status: State<SyncWeightState?> = mutableStatus

    fun syncWeight(inputStream: InputStream?) = viewModelScope.launch {
        mutableStatus.value = SyncWeightState.Uploading
        if (inputStream != null) {
            when (syncWeightUseCase(inputStream)) {
                is Result.Failure -> mutableStatus.value = SyncWeightState.Success
                is Result.Success -> mutableStatus.value = SyncWeightState.Failure
            }
        } else {
            mutableStatus.value = SyncWeightState.Failure
        }
        return@launch
    }
}