package paufregi.connectfeed.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.api.GarminConnect
import paufregi.connectfeed.data.api.models.EventType as DataEventType
import paufregi.connectfeed.data.api.models.Metadata
import paufregi.connectfeed.data.api.models.Summary
import paufregi.connectfeed.data.api.models.UpdateActivity
import paufregi.connectfeed.data.api.utils.callApi
import paufregi.connectfeed.data.database.GarminDao
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity
import paufregi.connectfeed.data.datastore.TokenManager
import java.io.File
import javax.inject.Inject
import kotlin.Int

class GarminRepository @Inject constructor(
    private val garminDao: GarminDao,
    private val garminConnect: GarminConnect,
    private val tokenManager: TokenManager
) {
    suspend fun saveCredential(credential: Credential) =
        garminDao.saveCredential(CredentialEntity(credential = credential))

    suspend fun saveProfile(profile: Profile) =
        garminDao.saveProfile(ProfileEntity(profile = profile))

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

    suspend fun getCourses(): Result<List<Course>> {
        return callApi (
            { garminConnect.getCourses() },
            { res -> res.body()?.map { it.toCore() } ?: emptyList() }
        )
    }

    suspend fun getEventTypes(): Result<List<EventType>> {
        return callApi (
            { garminConnect.getEventTypes() },
            { res -> res.body()?.map { it.toCore() }?.filterNotNull() ?: emptyList() }
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
            name = profile.name,
            eventType = DataEventType(profile.eventType?.id, profile.eventType?.name?.lowercase()),
            metadata = Metadata(profile.course?.id),
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