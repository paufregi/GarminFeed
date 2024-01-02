package paufregi.garminfeed.garmin.api

import paufregi.garminfeed.garmin.data.Oauth1Consumer
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface Garth {
    @GET("/oauth_consumer.json")
    suspend fun getOauthConsumer(): Response<Oauth1Consumer>

    companion object {
        private const val BASE_URL = "https://thegarth.s3.amazonaws.com"

        val client: Garth by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Garth::class.java)
        }
    }
}
