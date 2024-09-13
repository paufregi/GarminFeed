package paufregi.garminfeed.garmin

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import paufregi.garminfeed.garmin.api.GarminConnect
import paufregi.garminfeed.garmin.api.GarminSSO
import paufregi.garminfeed.garmin.api.Garth
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import paufregi.garminfeed.garmin.data.Oauth2
import java.io.File

class GarminClient(
    private val username: String,
    private val password: String,
    private var oauth1: Oauth1? = null,
    private var oauth2: Oauth2? = null,
    private val cacheOauth1: (Oauth1) -> Unit = {},
    private val cacheOauth2: (Oauth2) -> Unit = {}
) {
    private var oauth1Consumer: Oauth1Consumer? = null
    private suspend fun login(): Boolean {
        if (oauth2?.isExpired() == false) {
            Log.i("GARMIN", "Oauth2 token still valid")
            return true
        }
        if (oauth1?.isValid() == true) {
            return exchangeOauth()
        }

        Log.i("GARMIN", "Full login")
        val csrf = GarminSSO.client.getCSRF()
        if (!csrf.isSuccessful) {
            Log.e("GARMIN", "Failed to get CSRF: ${csrf.errorBody().toString()}")
            return false
        }

        val ticket = GarminSSO.client.login(username, password, csrf.body()!!)
        if (!ticket.isSuccessful) {
            Log.e("GARMIN", "Failed to get ticket: ${ticket.errorBody().toString()}")
            return false
        }

        if (oauth1Consumer == null) {
            val oauth1ConsumerRes = Garth.client.getOauthConsumer()
            if (!oauth1ConsumerRes.isSuccessful) {
                Log.e(
                    "GARMIN",
                    "Failed to get Oauth1 consumer: ${oauth1ConsumerRes.errorBody().toString()}"
                )
                return false
            }
            oauth1Consumer = oauth1ConsumerRes.body()!!
        }

        val oauth1Res = GarminConnect.client(oauth1Consumer!!).getOauth1Token(ticket.body()!!)
        if (!oauth1Res.isSuccessful) {
            Log.e("GARMIN", "Failed to get Oauth1: ${oauth1Res.errorBody().toString()}")
            return false
        }
        oauth1 = oauth1Res.body()!!
        cacheOauth1(oauth1!!)

        return exchangeOauth()
    }

    private suspend fun exchangeOauth(): Boolean {
        Log.i("GARMIN", "Exchange Oauth1 token")
        if (oauth1Consumer == null) {
            val oauth1ConsumerRes = Garth.client.getOauthConsumer()
            if (!oauth1ConsumerRes.isSuccessful) {
                Log.e(
                    "GARMIN",
                    "Failed to get Oauth1 consumer: ${oauth1ConsumerRes.errorBody().toString()}"
                )
                return false
            }
            oauth1Consumer = oauth1ConsumerRes.body()!!
        }
        val oauth2Res = GarminConnect.client(oauth1Consumer!!, oauth1!!).getOauth2Token()
        if (!oauth2Res.isSuccessful) {
            Log.e("GARMIN", "Failed to Exchange Oauth1: ${oauth2Res.errorBody().toString()}")
            return false
        }
        oauth2 = oauth2Res.body()!!
        cacheOauth2(oauth2!!)
        return true
    }

    suspend fun uploadFile(file: File): Boolean {
        Log.i("GARMIN", "Uploading fit file")
        if (oauth2 == null || oauth2?.isExpired() == true) {
            login()
        }
        val auth = "Bearer ${oauth2?.accessToken}"
        val fitFile = MultipartBody.Part.createFormData("fit", file.name, file.asRequestBody())

        val res = GarminConnect.client().uploadFile(auth, fitFile)
        return res.isSuccessful
    }
}