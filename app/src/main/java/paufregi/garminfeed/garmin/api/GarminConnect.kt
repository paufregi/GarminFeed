package paufregi.garminfeed.garmin.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import paufregi.garminfeed.garmin.converters.GarminConverterFactory
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import paufregi.garminfeed.garmin.data.Oauth2
import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor


interface GarminConnect {

    @GET("/oauth-service/oauth/preauthorized")
    @Headers(
        "User-Agent: com.garmin.android.apps.connectmobile"
    )
    suspend fun getOauth1Token(
        @Query("ticket") ticket: Ticket,
        @Query("login-url") loginUrl: String = "https://sso.garmin.com/sso/embed"
    ): Response<Oauth1>

    @POST("/oauth-service/oauth/exchange/user/2.0")
    @Headers(
        "User-Agent: com.garmin.android.apps.connectmobile",
        "Content-Type: application/x-www-form-urlencoded"
    )
    suspend fun getOauth2Token(): Response<Oauth2>

    @Multipart
    @POST("/upload-service/upload")
    suspend fun uploadFile(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part
    ): Response<Void>

    companion object {
        private const val BASE_URL = "https://connectapi.garmin.com"

        fun client(oauth1Consumer: Oauth1Consumer): GarminConnect {
            val consumer = OkHttpOAuthConsumer(oauth1Consumer.key, oauth1Consumer.secret)

            val client = OkHttpClient.Builder()
                .addInterceptor(SigningInterceptor(consumer))

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GarminConverterFactory())
                .client(client.build())
                .build()
                .create(GarminConnect::class.java)
        }

        fun client(oauthConsumer: Oauth1Consumer, oauth1: Oauth1): GarminConnect {
            val consumer = OkHttpOAuthConsumer(oauthConsumer.key, oauthConsumer.secret)
            consumer.setTokenWithSecret(oauth1.token, oauth1.secret)

            val client = OkHttpClient.Builder()
                .addInterceptor(SigningInterceptor(consumer))

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GarminConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(GarminConnect::class.java)
        }

        fun client(): GarminConnect {

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GarminConverterFactory())
                .build()
                .create(GarminConnect::class.java)
        }
    }
}
