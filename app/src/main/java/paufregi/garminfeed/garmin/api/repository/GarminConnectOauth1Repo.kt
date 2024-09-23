package paufregi.garminfeed.garmin.api.repository

import paufregi.garminfeed.garmin.api.GarminConnectOauth1
import paufregi.garminfeed.garmin.api.GarminSSO
import paufregi.garminfeed.garmin.api.Garth
import paufregi.garminfeed.garmin.data.ApiResponse
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import paufregi.garminfeed.garmin.data.Ticket
import javax.inject.Inject

class GarminConnectOauth1Repo @Inject constructor(
    private val garminSSO: GarminSSO,
    private val garminConnectOauth1: GarminConnectOauth1,
) {
    suspend fun getOauth1(ticket: Ticket): ApiResponse<Oauth1> {
        val res = garminConnectOauth1.getOauth1Token(ticket)
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }
}