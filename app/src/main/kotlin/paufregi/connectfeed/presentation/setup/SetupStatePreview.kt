package paufregi.connectfeed.presentation.setup

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.presentation.ui.models.ProcessState

class SetupContentStatePreview : PreviewParameterProvider<SetupState> {
    override val values = sequenceOf(
        SetupState(process = ProcessState.Processing),
        SetupState(process = ProcessState.Success("Paul")),
        SetupState(process = ProcessState.Failure("Error")),
    )
}

class SetupFormStatePreview : PreviewParameterProvider<SetupState> {
    override val values = sequenceOf(
        SetupState(credential = Credential("user", "pass")),
        SetupState(credential = Credential("user", "")),
        SetupState(credential = Credential("", "pass")),
        SetupState(credential = Credential("user", "pass"), showPassword = true),
    )
}