package paufregi.garminfeed.garmin.api

import paufregi.garminfeed.garmin.converters.GarminConverterFactory
import paufregi.garminfeed.garmin.data.CSRF
import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface GarminSSO {

    @GET("/sso/signin")
    @Headers(
        "accept: text/html",
        "referer: https://sso.garmin.com/sso/embed?id=gauth-widget&embedWidget=true&gauthHost=https%3A%2F%2Fsso.garmin.com%2Fsso"
    )
    suspend fun getCSRF(
        @QueryMap queryMap: Map<String, String> = defaultQueryMap,
    ): Response<CSRF>

    @FormUrlEncoded
    @POST("/sso/signin")
    @Headers(
        "accept: text/html",
        "referer: https://sso.garmin.com/sso/signin?id=gauth-widget&embedWidget=true&gauthHost=https%3A%2F%2Fsso.garmin.com%2Fsso%2Fembed&service=https%3A%2F%2Fsso.garmin.com%2Fsso%2Fembed&source=https%3A%2F%2Fsso.garmin.com%2Fsso%2Fembed&redirectAfterAccountLoginUrl=https%3A%2F%2Fsso.garmin.com%2Fsso%2Fembed&redirectAfterAccountCreationUrl=https%3A%2F%2Fsso.garmin.com%2Fsso%2Fembed",
    )
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("_csrf") csrf: CSRF,
        @Field("embed") embed: Boolean = true,
        @QueryMap queryMap: Map<String, String> = defaultQueryMap,
    ): Response<Ticket>

    companion object {
        private const val BASE_URL = "https://sso.garmin.com"

        val defaultQueryMap = mapOf(
            "id" to "gauth-widget",
            "embedWidget" to "true",
            "gauthHost" to "https://sso.garmin.com/sso/embed",
            "service" to "https://sso.garmin.com/sso/embed",
            "source" to "https://sso.garmin.com/sso/embed",
            "redirectAfterAccountLoginUrl" to "https://sso.garmin.com/sso/embed",
            "redirectAfterAccountCreationUrl" to "https://sso.garmin.com/sso/embed",
        )

        val client: GarminSSO by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GarminConverterFactory())
                .build()
                .create(GarminSSO::class.java)
        }
    }
}
