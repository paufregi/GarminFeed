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


interface GarminConnectOauth1 {

    @GET("/oauth-service/oauth/preauthorized")
    @Headers(
        "User-Agent: com.garmin.android.apps.connectmobile"
    )
    suspend fun getOauth1Token(
        @Query("ticket") ticket: Ticket,
        @Query("login-url") loginUrl: String = "https://sso.garmin.com/sso/embed"
    ): Response<Oauth1>

    companion object {
        const val BASE_URL = "https://connectapi.garmin.com"
    }
}
