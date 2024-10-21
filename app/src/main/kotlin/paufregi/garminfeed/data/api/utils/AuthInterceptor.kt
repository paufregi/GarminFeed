package paufregi.garminfeed.data.api.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.api.GarminConnectOAuth1
import paufregi.garminfeed.data.api.GarminConnectOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.datastore.TokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val garminDao: GarminDao,
    private val garth: Garth,
    private val garminSSO: GarminSSO,
    private val tokenManager: TokenManager,
    private val createConnectOAuth1: (oauthConsumer: OAuthConsumer) -> GarminConnectOAuth1,
    private val createConnectOAuth2: (oauthConsumer: OAuthConsumer, oauth: OAuth1) -> GarminConnectOAuth2,
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cachedOauth2 = runBlocking { tokenManager.getOauth2().first() }
        return if (cachedOauth2 != null && !cachedOauth2.isExpired()) {
            chain.proceed(newRequestWithAccessToken(cachedOauth2.accessToken, request))
        } else {
            when (val oauth2 = runBlocking { authenticate() }){
                is Result.Success -> chain.proceed(newRequestWithAccessToken(oauth2.data.accessToken, request))
                is Result.Failure -> Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(401)
                    .message("Auth failed")
                    .body("".toResponseBody())
                    .build()
            }
        }
    }

    private suspend fun getCSRF(): Result<CSRF> {
        val res = garminSSO.getCSRF()
        return when(res.isSuccessful) {
            true -> Result.Success(res.body()!!)
            false -> Result.Failure(res.errorBody().toString())
        }
    }

    private suspend fun login(username: String, password: String, csrf: CSRF): Result<Ticket> {
        val res = garminSSO.login(username = username, password = password, csrf = csrf)
        return when(res.isSuccessful) {
            true -> Result.Success(res.body()!!)
            false -> Result.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuthConsumer(): Result<OAuthConsumer> {
        val res = garth.getOAuthConsumer()
        return when(res.isSuccessful) {
            true -> Result.Success(res.body()!!)
            false -> Result.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuthToken(ticket: Ticket, consumer: OAuthConsumer): Result<OAuth1> {
        val garminConnect = createConnectOAuth1(consumer)
        val res = garminConnect.getOauth1(ticket)
        return when(res.isSuccessful) {
            true -> Result.Success(res.body()!!)
            false -> Result.Failure(res.errorBody().toString())
        }
    }

    private suspend fun getOAuth2Token(oauth: OAuth1, consumer: OAuthConsumer): Result<OAuth2> {
        val garminConnect = createConnectOAuth2(consumer, oauth)
        val res = garminConnect.getOauth2()
        return when(res.isSuccessful) {
            true -> Result.Success(res.body()!!)
            false -> Result.Failure(res.errorBody().toString())
        }
    }

    private suspend fun signIn(): Result<Ticket> {
        val cred = garminDao.getCredential().lastOrNull()?.credential ?: return Result.Failure("No credentials")
        return when(val csrf = getCSRF()) {
            is Result.Success -> login(username = cred.username , password = cred.password, csrf = csrf.data)
            is Result.Failure -> Result.Failure(csrf.error)
        }
    }

    private suspend fun authenticate(): Result<OAuth2> {

        val consumer =
            tokenManager.getOAuthConsumer().first() ?: when (val res = getOAuthConsumer()) {
                is Result.Success -> {
                    tokenManager.saveOAuthConsumer(res.data)
                    res.data
                }
                is Result.Failure -> return Result.Failure(res.error)
            }

        val cachedOAuth1 = tokenManager.getOauth1().first()
        val oauth: OAuth1
        if (cachedOAuth1 != null && cachedOAuth1.isValid()) {
            oauth = cachedOAuth1
        } else {
            val ticket = when (val res = signIn()) {
                is Result.Success -> res.data
                is Result.Failure -> return Result.Failure(res.error)
            }

            oauth = when (val res = getOAuthToken(ticket, consumer)) {
                is Result.Success -> {
                    tokenManager.saveOAuth1(res.data)
                    res.data
                }
                is Result.Failure -> return Result.Failure(res.error)
            }
        }

        return when (val oauth2 = getOAuth2Token(oauth, consumer)) {
            is Result.Success -> {
                tokenManager.saveOAuth2(oauth2.data)
                Result.Success(oauth2.data)
            }
            is Result.Failure -> Result.Failure(oauth2.error)
        }
    }

    private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
}