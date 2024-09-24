package paufregi.garminfeed.data.api

import okhttp3.OkHttpClient
import paufregi.garminfeed.data.api.converters.GarminConverterFactory
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor


interface GarminConnectOAuth2 {

    @POST("/oauth-service/oauth/exchange/user/2.0")
    @Headers(
        "User-Agent: com.garmin.android.apps.connectmobile",
        "Content-Type: application/x-www-form-urlencoded"
    )
    suspend fun getOauth2Token(): Response<OAuth2>

    companion object {
        private const val BASE_URL = "https://connectapi.garmin.com"

        fun client(oAuthConsumer: OAuthConsumer, oauth: OAuth1): GarminConnectOAuth2 {
            val consumer = OkHttpOAuthConsumer(oAuthConsumer.key, oAuthConsumer.secret)
            consumer.setTokenWithSecret(oauth.token, oauth.secret)

            val client = OkHttpClient.Builder()
                .addInterceptor(SigningInterceptor(consumer))

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GarminConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(GarminConnectOAuth2::class.java)
        }
    }
}
