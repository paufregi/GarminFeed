package paufregi.connectfeed.core.models

sealed interface Result<T> {
    val isSuccessful: Boolean

    class Success<T>(val data: T) : Result<T> {
        override val isSuccessful: Boolean
            get() = true
    }
    class Failure<T>(val error: String) : Result<T>{
        override val isSuccessful: Boolean
            get() = false
    }
}