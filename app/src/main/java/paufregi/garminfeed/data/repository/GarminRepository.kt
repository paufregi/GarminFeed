package paufregi.garminfeed.data.repository

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.database.GarminDao
import java.io.File

import javax.inject.Inject

class GarminRepository @Inject constructor(
    private val garminDao: GarminDao,
    private val garminConnect: GarminConnect
) {
    suspend fun saveCredentials(credentials: Credentials) = garminDao.saveCredentials(credentials)

    suspend fun getCredentials(): Credentials? = garminDao.getCredentials()

    suspend fun uploadFile(file: File): ApiResponse<Unit> {
        Log.i("GARMIN", "Uploading fit file")
        val fitFile = MultipartBody.Part.createFormData("fit", file.name, file.asRequestBody())

        val res = garminConnect.uploadFile(fitFile)
        return when (res.isSuccessful){
            true -> ApiResponse.Success(Unit)
            false -> ApiResponse.Failure(res.errorBody().toString())
        }
    }
}