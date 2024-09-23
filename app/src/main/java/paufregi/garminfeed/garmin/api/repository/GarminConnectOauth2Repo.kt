package paufregi.garminfeed.garmin.api.repository

import paufregi.garminfeed.garmin.api.GarminConnectOauth2
import paufregi.garminfeed.garmin.data.ApiResponse
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import paufregi.garminfeed.garmin.data.Oauth2
import javax.inject.Inject

class GarminConnectOauth2Repo @Inject constructor(
    private val garminConnectOauth2: GarminConnectOauth2
) {
    suspend fun getOauth2(): ApiResponse<Oauth2> {
        val res = garminConnectOauth2.getOauth2Token()
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }
}