package paufregi.connectfeed.data.api.utils

import android.util.Log
import paufregi.connectfeed.core.models.Result
import retrofit2.Response

suspend fun <T, R>callApi(block: suspend () -> Response<T>, transform: (Response<T>) -> R): Result<R> {
    return runCatching { block() }
        .map{ res ->
            when(res.isSuccessful) {
                true -> Result.Success(transform(res))
                false -> {
                    Log.e("ConnectFeed", res.errorBody()?.string() ?: "no errorBody")
                    Result.Failure(res.errorBody()?.string() ?: "no errorBody")
                }
            }
        }
        .getOrElse {
            error ->
                Log.e("ConnectFeed", error.message ?: "Unknown error", error)
                Result.Failure(error.message ?: "Unknown error")
        }
}