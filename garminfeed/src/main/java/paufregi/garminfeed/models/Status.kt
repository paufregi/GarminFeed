package paufregi.garminfeed.models

sealed class Status {
    data object Uploading : Status()
    data object Success : Status()
    data object Failure : Status()
}