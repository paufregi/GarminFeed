package paufregi.connectfeed.data.api.utils

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.api.GarminConnectOAuth1
import paufregi.connectfeed.data.api.GarminConnectOAuth2
import paufregi.connectfeed.data.api.GarminSSO
import paufregi.connectfeed.data.api.Garth
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.OAuth2
import paufregi.connectfeed.data.api.models.OAuthConsumer
import paufregi.connectfeed.data.datastore.UserDataStore
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val garth: Garth,
    private val garminSSO: GarminSSO,
    private val userDataStore: UserDataStore,
    private val createConnectOAuth1: (oauthConsumer: OAuthConsumer) -> GarminConnectOAuth1,
    private val createConnectOAuth2: (oauthConsumer: OAuthConsumer, oauth: OAuth1) -> GarminConnectOAuth2,
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var oAuth2 = runBlocking { userDataStore.getOauth2().firstOrNull() }
        if (oAuth2 == null || oAuth2.isExpired()) {
            val resOAuth2 = runBlocking { authenticate() }
            when (resOAuth2) {
                is Result.Failure -> return failedResponse(request, resOAuth2.reason)
                is Result.Success -> oAuth2 = resOAuth2.data
            }
        }

        return chain.proceed(authRequest(request, oAuth2.accessToken))
    }

    private suspend fun login(consumer: OAuthConsumer): Result<OAuth1> {
        val credential = userDataStore.getCredential().firstOrNull()
        if (credential == null) return Result.Failure("No credential found")

        val resCSRF = garminSSO.getCSRF()
        if (!resCSRF.isSuccessful) return Result.Failure("Problem with the login page")
        val csrf = resCSRF.body()!!

        val resLogin = garminSSO.login(username = credential.username, password = credential.password, csrf = csrf)
        if (!resLogin.isSuccessful) return Result.Failure("Couldn't login")
        val ticket = resLogin.body()!!

        val connect = createConnectOAuth1(consumer)
        val resOAuth1 = connect.getOauth1(ticket)
        if (!resOAuth1.isSuccessful) return Result.Failure("Couldn't get OAuth1 token")
        return Result.Success(resOAuth1.body()!!)
    }

    private suspend fun authenticate(): Result<OAuth2> {
        var consumer: OAuthConsumer? = userDataStore.getOAuthConsumer().firstOrNull()
        if (consumer == null) {
            val resConsumer = garth.getOAuthConsumer()
            if (!resConsumer.isSuccessful) return Result.Failure("Couldn't get OAuth Consumer")
            consumer = resConsumer.body()!!
            userDataStore.saveOAuthConsumer(consumer)
        }

        var oauth1 = userDataStore.getOauth1().firstOrNull()
        if (oauth1 == null) {
            val resOAuth1 = login(consumer)
            when (resOAuth1) {
                is Result.Failure -> return Result.Failure(resOAuth1.reason)
                is Result.Success -> oauth1 = resOAuth1.data
            }
            userDataStore.saveOAuth1(resOAuth1.data)
        }

        val connect = createConnectOAuth2(consumer, oauth1)
        val resOAuth2 = connect.getOauth2()
        if (!resOAuth2.isSuccessful) return Result.Failure("Couldn't get OAuth2 token")
        val oauth2 = resOAuth2.body()!!
        userDataStore.saveOAuth2(oauth2)
        return Result.Success(oauth2)
    }

    private fun authRequest(request: Request, accessToken: String?): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

    private fun failedResponse(request: Request, reason: String): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Auth failed")
            .body(reason.toResponseBody())
            .build()
}