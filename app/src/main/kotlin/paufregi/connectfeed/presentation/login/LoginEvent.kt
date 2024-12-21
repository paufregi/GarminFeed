package paufregi.connectfeed.presentation.login


sealed interface LoginEvent {
    data class SetUsername(val username: String): LoginEvent
    data class SetPassword(val password: String): LoginEvent
    data class ShowPassword(val showPassword: Boolean): LoginEvent
    data object Reset: LoginEvent
    data object SignIn: LoginEvent
}
