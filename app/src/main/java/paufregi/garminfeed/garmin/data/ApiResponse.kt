package paufregi.garminfeed.garmin.data

sealed interface ApiResponse<T> {
    class Success<T>(val data: T): ApiResponse<T>
    class Failure<T>(val error: String): ApiResponse<T>
}


