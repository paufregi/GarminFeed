package paufregi.garminfeed.presentation.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.core.models.Credential

class SettingsStatePreview : PreviewParameterProvider<MutableState<SettingsState>> {
    override val values = sequenceOf(
        mutableStateOf(SettingsState(Credential("paul", "pass"), false)),
        mutableStateOf(SettingsState(Credential("paul", "pass"), true)),
        mutableStateOf(SettingsState(Credential("", "pass"), false)),
        mutableStateOf(SettingsState(Credential("paul", ""), false)),
        mutableStateOf(SettingsState(Credential("", ""), false)),
        mutableStateOf(SettingsState(Credential("", ""), true))
    )
}