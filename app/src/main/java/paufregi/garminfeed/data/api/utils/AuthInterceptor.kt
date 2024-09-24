package paufregi.garminfeed.data.api.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.repository.GarminAuthRepository
import paufregi.garminfeed.data.utils.TokenManager
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authRepository: GarminAuthRepository,
    private val tokenManager: TokenManager
): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val cachedOauth2 = runBlocking { tokenManager.getOauth2().first() }
        if (cachedOauth2 != null && cachedOauth2.isExpired()) {
            return chain.proceed(newRequestWithAccessToken(cachedOauth2.accessToken, request))
        } else {
            when (val oAuth2 = runBlocking { authRepository.authenticate() }){
                is ApiResponse.Success -> return chain.proceed(newRequestWithAccessToken(oAuth2.data.accessToken, request))
                is ApiResponse.Failure -> return chain.proceed(request)
            }
        }
    }

    private fun newRequestWithAccessToken(accessToken: String?, request: Request): Request =
        request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
}