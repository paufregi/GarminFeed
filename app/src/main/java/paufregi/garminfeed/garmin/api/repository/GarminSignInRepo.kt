package paufregi.garminfeed.garmin.api.repository

import paufregi.garminfeed.garmin.api.GarminSSO
import paufregi.garminfeed.garmin.data.ApiResponse
import paufregi.garminfeed.garmin.data.CSRF
import paufregi.garminfeed.garmin.data.Ticket
import javax.inject.Inject

class GarminSignInRepo @Inject constructor(
    private val garminSSO: GarminSSO,
) {
    private suspend fun getCSRF(): ApiResponse<CSRF> {
        val res = garminSSO.getCSRF()
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    private suspend fun login(username: String, password: String, csrf: CSRF): ApiResponse<Ticket> {
        val res = garminSSO.login(username = username, password = password, csrf = csrf)
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    suspend fun signIn(username: String, password: String): ApiResponse<Ticket> {
        return when(val csfr = getCSRF()) {
            is ApiResponse.Success -> login(username = username, password = password, csrf = csfr.data)
            is ApiResponse.Failure -> ApiResponse.Failure(csfr.error)
        }
    }
}