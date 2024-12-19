package paufregi.connectfeed.presentation.account

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.presentation.ui.components.ProcessState

class AccountStatePreview : PreviewParameterProvider<AccountState> {
    override val values = sequenceOf(
        AccountState(processState = ProcessState.Processing),
        AccountState(processState = ProcessState.Success("Profile saved")),
        AccountState(processState = ProcessState.Failure("Error saving profile")),
        AccountState(processState = ProcessState.Idle),
    )
}