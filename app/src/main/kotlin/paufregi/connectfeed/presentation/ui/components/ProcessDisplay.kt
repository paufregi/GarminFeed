package paufregi.connectfeed.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable

sealed interface ProcessState {
    data object Idle : ProcessState
    data object Processing : ProcessState
    data class Success(val message: String) : ProcessState
    data class Failure(val reason: String) : ProcessState
}

data class Info (
    val text: String,
    val actionButton: @Composable () -> Unit
)

@Composable
@ExperimentalMaterial3Api
fun ProcessDisplay (
    state: ProcessState,
    successInfo: (ProcessState.Success) -> Info,
    failureInfo: (ProcessState.Failure) -> Info,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable (PaddingValues) -> Unit = {}
){
    when (state) {
        is ProcessState.Processing -> Loading(paddingValues)
        is ProcessState.Success -> {
            val info = successInfo(state)
            StatusInfo(
                type = StatusInfoType.Success,
                text = info.text,
                actionButton = info.actionButton,
                paddingValues = paddingValues)
        }
        is ProcessState.Failure -> {
            val info = failureInfo(state)
            StatusInfo(
                type = StatusInfoType.Failure,
                text = info.text,
                actionButton = info.actionButton,
                paddingValues = paddingValues)
        }
        else -> content(paddingValues)
    }
}

