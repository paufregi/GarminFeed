package paufregi.garminfeed.core.models

sealed class ImportStatus {
    data object Uploading : ImportStatus()
    data object Success : ImportStatus()
    data object Failure : ImportStatus()
}