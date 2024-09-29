package paufregi.garminfeed.data.api.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.repository.GarminAuthRepository
import paufregi.garminfeed.data.datastore.TokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: GarminAuthRepository,
    private val tokenManager: TokenManager
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val cachedOauth2 = runBlocking { tokenManager.getOauth2().first() }
        return if (cachedOauth2 != null && !cachedOauth2.isExpired()) {
            chain.proceed(newRequestWithAccessToken(cachedOauth2.accessToken, request))
        } else {
            when (val oAuth2 = runBlocking { authRepository.authenticate() }){
                is ApiResponse.Success -> chain.proceed(newRequestWithAccessToken(oAuth2.data.accessToken, request))
                is ApiResponse.Failure -> Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(401).message("Auth failed").body("".toResponseBody()).build()
            }
        }
    }

    private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
}