package paufregi.connectfeed.presentation.setup


sealed interface SetupEvent {
    data class SetUsername(val username: String): SetupEvent
    data class SetPassword(val password: String): SetupEvent
    data class ShowPassword(val showPassword: Boolean): SetupEvent
    data object Reset: SetupEvent
    data object Done: SetupEvent
    data object Save: SetupEvent
}
