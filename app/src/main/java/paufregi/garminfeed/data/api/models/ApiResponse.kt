package paufregi.garminfeed.data.api.models

import kotlinx.coroutines.runBlocking

sealed interface ApiResponse<T> {
    val isSuccessful: Boolean

    class Success<T>(val data: T) : ApiResponse<T> {
        override val isSuccessful: Boolean
            get() = true
    }
    class Failure<T>(val error: String) : ApiResponse<T>{
        override val isSuccessful: Boolean
            get() = false
    }
}