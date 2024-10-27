package paufregi.garminfeed.data.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import paufregi.garminfeed.data.api.converters.GarminConverterFactory
import paufregi.garminfeed.data.api.models.Activity
import paufregi.garminfeed.data.api.models.UpdateActivityRequest
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface GarminConnect {

    @Multipart
    @POST("/upload-service/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<Unit>

    @GET("/activitylist-service/activities/search/activities")
    suspend fun getLatestActivity(
        @Query("limit") limit: Int,
        @Query("start") start: Int = 0,
    ): Response<List<Activity>>

    @PUT("/activity-service/activity/{id}")
    suspend fun updateActivity(
        @Path("id") id: Long,
        @Body updateActivityRequest: UpdateActivityRequest,
    ): Response<Unit>

    companion object {
        const val BASE_URL = "https://connectapi.garmin.com"

        fun client(authInterceptor: AuthInterceptor, url: String): GarminConnect {
            val client = OkHttpClient.Builder().addInterceptor(authInterceptor)

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
