package paufregi.connectfeed.presentation.setup

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.presentation.ui.models.ProcessState

data class SetupState(
    val process: ProcessState = ProcessState.Idle,
    val credential: Credential = Credential(),
    val showPassword: Boolean = false,
)

fun MutableStateFlow<SetupState>.change(
    processState: ProcessState? = null,
    username: String? = null,
    password: String? = null,
    credential: Credential? = null,
    showPassword: Boolean?  = null,
) = this.update { it.copy(
        process = processState ?: it.process,
        credential = credential ?: Credential(
            username ?: it.credential.username,
            password ?: it.credential.password),
        showPassword = showPassword ?: it.showPassword)
}
