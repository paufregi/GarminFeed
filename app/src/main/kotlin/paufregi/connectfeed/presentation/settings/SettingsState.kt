package paufregi.connectfeed.presentation.settings

import paufregi.connectfeed.core.models.Credential

data class SettingsState(
    val credential: Credential = Credential(),
    val showPassword: Boolean = false,
)
