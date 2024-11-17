package paufregi.connectfeed.presentation.profiles


sealed class ProfilesEvent {
    data class Save(val callback: () -> Unit): ProfilesEvent()
}
