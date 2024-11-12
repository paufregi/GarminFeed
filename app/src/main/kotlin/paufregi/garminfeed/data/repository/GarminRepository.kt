package paufregi.garminfeed.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.api.models.EventType
import paufregi.garminfeed.data.api.models.Metadata
import paufregi.garminfeed.data.api.models.Summary
import paufregi.garminfeed.data.api.models.UpdateActivity
import paufregi.garminfeed.data.api.utils.callApi
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

    fun getCredential(): Flow<Credential?> =
        garminDao.getCredential().map { it?.credential }

    suspend fun clearCache() {
        tokenManager.deleteOAuth1()
        tokenManager.deleteOAuth2()
    }

    suspend fun getLatestActivities(limit: Int): Result<List<Activity>> {
        return callApi (
            { garminConnect.getLatestActivity(limit) },
            { res -> res.body()?.map { it.toCore() } ?: emptyList() }
        )
    }

    suspend fun updateActivity(
        activity: Activity,
        profile: Profile,
        effort: Float?,
        feel: Float?,
    ): Result<Unit> {
        val request = UpdateActivity(
            id = activity.id,
            name = profile.activityName,
            eventType = EventType(profile.eventType.id, profile.eventType.name.lowercase()),
            metadata = Metadata(profile.course.id),
            summary = Summary(profile.water, effort, feel)
        )
        return callApi(
            { garminConnect.updateActivity(activity.id, request) },
            { _ -> Result.Success(Unit) }
        )
    }

    suspend fun uploadFile(file: File): Result<Unit> {
        val multipartBody = MultipartBody.Part.createFormData("fit", file.name, file.asRequestBody())
        return callApi(
            { garminConnect.uploadFile(multipartBody) },
            { _ -> Result.Success(Unit)}
        )
    }
}