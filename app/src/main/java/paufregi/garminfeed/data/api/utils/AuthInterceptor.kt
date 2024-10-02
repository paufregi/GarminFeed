package paufregi.garminfeed.data.api.utils

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
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
import javax.inject.Named

class AuthInterceptor @Inject constructor(
    private val garminDao: GarminDao,
    private val garth: Garth,
    private val garminSSO: GarminSSO,
    private val tokenManager: TokenManager,
    @Named("GarminConnectOAuth1Url")private val garminConnectOAuth1Url: String,
    @Named("GarminConnectOAuth2Url")private val garminConnectOAuth2Url: String
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val cachedOauth2 = runBlocking { tokenManager.getOauth2().first() }
        return if (cachedOauth2 != null && !cachedOauth2.isExpired()) {
            chain.proceed(newRequestWithAccessToken(cachedOauth2.accessToken, request))
        } else {
            when (val oauth2 = runBlocking { authenticate() }){
                is ApiResponse.Success -> chain.proceed(newRequestWithAccessToken(oauth2.data.accessToken, request))
                is ApiResponse.Failure -> Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Auth failed")
                    .body("".toResponseBody())
                    .build()
            }
        }
    }

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
        val garminConnect = GarminConnectOAuth1.client(consumer, garminConnectOAuth1Url)
        val res = garminConnect.getOauth1(ticket)
        return when(res.isSuccessful) {
            true -> ApiResponse.Success(res.body()!!)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuth2Token(oauth: OAuth1, consumer: OAuthConsumer): ApiResponse<OAuth2> {
        val garminConnect = GarminConnectOAuth2.client(consumer, oauth, garminConnectOAuth2Url)
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

    private suspend fun authenticate(): ApiResponse<OAuth2> {
        val consumer =
            tokenManager.getOAuthConsumer().first() ?: when (val res = getOAuthConsumer()) {
                is ApiResponse.Success -> {
                    tokenManager.saveOAuthConsumer(res.data)
                    res.data
                }
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }

        val cachedOAuth1 = tokenManager.getOauth1().first()
        val oauth: OAuth1
        if (cachedOAuth1 != null && cachedOAuth1.isValid()) {
            oauth = cachedOAuth1
        } else {
            val ticket = when (val res = signIn()) {
                is ApiResponse.Success -> res.data
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }

            oauth = when (val res = getOAuthToken(ticket, consumer)) {
                is ApiResponse.Success -> {
                    tokenManager.saveOAuth1(res.data)
                    res.data
                }
                is ApiResponse.Failure -> return ApiResponse.Failure(res.error)
            }
        }

        return when (val oauth2 = getOAuth2Token(oauth, consumer)) {
            is ApiResponse.Success -> {
                tokenManager.saveOAuth2(oauth2.data)
                ApiResponse.Success(oauth2.data)
            }
            is ApiResponse.Failure -> ApiResponse.Failure(oauth2.error)
        }
    }

    private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
}