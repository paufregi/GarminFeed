package paufregi.garminfeed.presentation.settings

data class SettingsState(
    val username: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
)
