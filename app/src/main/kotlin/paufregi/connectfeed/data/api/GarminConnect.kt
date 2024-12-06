package paufregi.connectfeed.data.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import paufregi.connectfeed.data.api.converters.GarminConverterFactory
import paufregi.connectfeed.data.api.models.Activity
import paufregi.connectfeed.data.api.models.Course
import paufregi.connectfeed.data.api.models.EventType
import paufregi.connectfeed.data.api.models.UpdateActivity
import paufregi.connectfeed.data.api.utils.AuthInterceptor
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

    @GET("/course-service/course")
    suspend fun getCourses(): Response<List<Course>>

    @GET("/activity-service/activity/eventTypes")
    suspend fun getEventTypes(): Response<List<EventType>>

    @PUT("/activity-service/activity/{id}")
    suspend fun updateActivity(
        @Path("id") id: Long,
        @Body updateActivity: UpdateActivity,
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
