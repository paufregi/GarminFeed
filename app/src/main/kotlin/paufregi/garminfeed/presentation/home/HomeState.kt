package paufregi.garminfeed.presentation.home

data class HomeState(
    val setupDone: Boolean = false,
    val clearCacheMessage: String? = null
)
