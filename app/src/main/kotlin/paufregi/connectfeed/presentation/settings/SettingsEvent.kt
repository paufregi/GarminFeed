package paufregi.connectfeed.presentation.settings


sealed interface SettingsEvent {
    data class SetUsername(val username: String): SettingsEvent
    data class SetPassword(val password: String): SettingsEvent
    data class SetShowPassword(val showPassword: Boolean): SettingsEvent
    data object Save: SettingsEvent
}
