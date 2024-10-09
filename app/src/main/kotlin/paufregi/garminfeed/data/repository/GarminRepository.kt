package paufregi.garminfeed.data.repository

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.data.datastore.TokenManager
import java.io.File

import javax.inject.Inject

class GarminRepository @Inject constructor(
    private val garminDao: GarminDao,
    private val garminConnect: GarminConnect,
    private val tokenManager: TokenManager
) {
    suspend fun saveCredential(credential: Credential) =
        garminDao.saveCredential(CredentialEntity(credential = credential))

    suspend fun getCredential(): Credential? =
        garminDao.getCredential()?.credential


    suspend fun clearCache() {
        tokenManager.deleteOAuth1()
        tokenManager.deleteOAuth2()
    }

    suspend fun uploadFile(file: File): Result<Unit> {
        Log.i("GARMIN", "Uploading fit file")
        val multipartBody = MultipartBody.Part.createFormData("fit", file.name, file.asRequestBody())

        val res = garminConnect.uploadFile(multipartBody)
        return when (res.isSuccessful){
            true -> Result.Success(Unit)
            false -> Result.Failure(res.errorBody().toString())
        }
    }
}