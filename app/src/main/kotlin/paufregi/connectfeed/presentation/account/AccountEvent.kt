package paufregi.connectfeed.presentation.account


sealed interface AccountEvent {
    data object ChangePassword: AccountEvent
    data object RefreshTokens: AccountEvent
    data object SignOut: AccountEvent
}
