package paufregi.garminfeed.presentation.utils.preview

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.presentation.settings.SettingsState

class SettingsStatePreview : PreviewParameterProvider<MutableState<SettingsState>> {
    override val values = sequenceOf(
        mutableStateOf(SettingsState("paul", "pass", false)),
        mutableStateOf(SettingsState("paul", "pass", true)),
        mutableStateOf(SettingsState("", "pass", false)),
        mutableStateOf(SettingsState("paul", "", false)),
        mutableStateOf(SettingsState("", "", false))
    )
}