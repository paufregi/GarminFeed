package paufregi.connectfeed.presentation.settings


sealed class SettingsEvent {
    data class UpdateUsername(val username: String): SettingsEvent()
    data class UpdatePassword(val password: String): SettingsEvent()
    data class UpdateShowPassword(val showPassword: Boolean): SettingsEvent()
    data object SaveCredential: SettingsEvent()
}
