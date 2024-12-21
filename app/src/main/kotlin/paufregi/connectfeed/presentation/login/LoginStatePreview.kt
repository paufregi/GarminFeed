package paufregi.connectfeed.presentation.login

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.presentation.ui.models.ProcessState

class SetupContentStatePreview : PreviewParameterProvider<LoginState> {
    override val values = sequenceOf(
        LoginState(process = ProcessState.Processing),
        LoginState(process = ProcessState.Success("Paul")),
        LoginState(process = ProcessState.Failure("Error")),
    )
}

class SetupFormStatePreview : PreviewParameterProvider<LoginState> {
    override val values = sequenceOf(
        LoginState(credential = Credential("user", "pass")),
        LoginState(credential = Credential("user", "")),
        LoginState(credential = Credential("", "pass")),
        LoginState(credential = Credential("user", "pass"), showPassword = true),
    )
}