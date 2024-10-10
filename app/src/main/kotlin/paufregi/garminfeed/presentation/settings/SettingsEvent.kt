package paufregi.garminfeed.presentation.settings


sealed class SettingsEvent {
    data class UpdateUsername(val username: String): SettingsEvent()
    data class UpdatePassword(val password: String): SettingsEvent()
    data class UpdateShowPassword(val showPassword: Boolean): SettingsEvent()
    object SaveCredential: SettingsEvent()
}
