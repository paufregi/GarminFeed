package paufregi.connectfeed.presentation.account

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.presentation.ui.models.ProcessState

class AccountStatePreview : PreviewParameterProvider<AccountState> {
    override val values = sequenceOf(
        AccountState(process = ProcessState.Processing),
        AccountState(process = ProcessState.Success("Profile saved")),
        AccountState(process = ProcessState.Failure("Error saving profile")),
        AccountState(process = ProcessState.Idle),
    )
}