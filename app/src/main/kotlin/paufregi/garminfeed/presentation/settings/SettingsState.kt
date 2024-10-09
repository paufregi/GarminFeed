package paufregi.garminfeed.presentation.settings

import paufregi.garminfeed.core.models.Credential

data class SettingsState(
    val credential: Credential = Credential(),
    val showPassword: Boolean = false,
)
