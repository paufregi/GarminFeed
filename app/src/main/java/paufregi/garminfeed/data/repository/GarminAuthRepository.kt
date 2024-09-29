package paufregi.garminfeed.data.repository

import kotlinx.coroutines.flow.first
import paufregi.garminfeed.data.api.GarminConnectOAuth1
import paufregi.garminfeed.data.api.GarminConnectOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.datastore.TokenManager
import javax.inject.Inject

class GarminAuthRepository @Inject constructor(
    private val garminDao: GarminDao,
    private val garminSSO: GarminSSO,
    private val garth: Garth,
    private val tokenManager: TokenManager
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

    private suspend fun getOAuthConsumer(): ApiResponse<OAuthConsumer> {
        val res = garth.getOAuthConsumer()
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuthToken(ticket: Ticket, consumer: OAuthConsumer): ApiResponse<OAuth1> {
        val garminConnect = GarminConnectOAuth1.client(consumer)

        val res = garminConnect.getOauth1(ticket)

        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuth2Token(oAuth: OAuth1, consumer: OAuthConsumer): ApiResponse<OAuth2> {
        val garminConnect = GarminConnectOAuth2.client(consumer, oAuth)

        val res = garminConnect.getOauth2Token()

        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    private suspend fun signIn(): ApiResponse<Ticket> {
        val credentials = garminDao.getCredentials() ?: return ApiResponse.Failure("No credentials")

        return when(val csfr = getCSRF()) {
            is ApiResponse.Success -> login(username = credentials.username, password = credentials.password, csrf = csfr.data)
            is ApiResponse.Failure -> ApiResponse.Failure(csfr.error)
        }
    }

    suspend fun authenticate(): ApiResponse<OAuth2> {

        val consumer =
            tokenManager.getOAuthConsumer().first() ?: when (val res = getOAuthConsumer()) {
                is ApiResponse.Success -> {
                    tokenManager.saveOAuthConsumer(res.data)
                    res.data
                }
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }


        val cachedOAuth1 = tokenManager.getOauth1().first()
        val oAuth: OAuth1
        if (cachedOAuth1 != null && cachedOAuth1.isValid()) {
            oAuth = cachedOAuth1
        } else {
            val ticket = when (val res = signIn()) {
                is ApiResponse.Success -> res.data
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }

            oAuth = when (val res = getOAuthToken(ticket, consumer)) {
                is ApiResponse.Success -> {
                    tokenManager.saveOAuth1(res.data)
                    res.data
                }
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }
        }

        return when (val oAuth2 = getOAuth2Token(oAuth, consumer)) {
            is ApiResponse.Success -> {
                tokenManager.saveOAuth2(oAuth2.data)
                ApiResponse.Success(oAuth2.data)
            }
            is ApiResponse.Failure -> ApiResponse.Failure(oAuth2.error)
        }
    }
}