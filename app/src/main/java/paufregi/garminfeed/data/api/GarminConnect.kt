package paufregi.garminfeed.data.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import paufregi.garminfeed.data.api.converters.GarminConverterFactory
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface GarminConnect {

    @Multipart
    @POST("/upload-service/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<Void>

    companion object {
        const val BASE_URL = "https://connectapi.garmin.com"

        fun client(authInterceptor: AuthInterceptor, url: String): GarminConnect {
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)

            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GarminConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
                .create(GarminConnect::class.java)
        }

    }
}
