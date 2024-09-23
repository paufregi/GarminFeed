package paufregi.garminfeed.garmin.api.repository

import paufregi.garminfeed.garmin.api.Garth
import paufregi.garminfeed.garmin.data.ApiResponse
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import javax.inject.Inject

class GarthRepo @Inject constructor(
    private val garth: Garth,
) {
    suspend fun getOauth1Consumer(): ApiResponse<Oauth1Consumer> {
        val res = garth.getOauthConsumer()
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }
}