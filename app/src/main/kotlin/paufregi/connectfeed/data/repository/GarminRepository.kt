package paufregi.connectfeed.data.repository

import androidx.compose.ui.util.fastMap
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
import paufregi.connectfeed.data.database.coverters.toCore
import paufregi.connectfeed.data.database.coverters.toEntity
import paufregi.connectfeed.data.datastore.UserDataStore
import java.io.File
import javax.inject.Inject
import kotlin.Int

class GarminRepository @Inject constructor(
    private val garminDao: GarminDao,
    private val garminConnect: GarminConnect,
    private val userDataStore: UserDataStore
) {
    fun getSetup(): Flow<Boolean> =
        userDataStore.getSetup()

    suspend fun saveSetup(setup: Boolean) =
        userDataStore.saveSetup(setup)

    suspend fun fetchFullName(): Result<String> =
        callApi (
            { garminConnect.getUserProfile() },
            { res -> res.body()?.firstName ?: "" }
        )

    fun getCredential(): Flow<Credential?> =
        userDataStore.getCredential()

    suspend fun saveCredential(credential: Credential) =
        userDataStore.saveCredential(credential)

    suspend fun deleteCredential() =
        userDataStore.deleteCredential()

    fun getAllProfiles(): Flow<List<Profile>> =
        garminDao.getAllProfiles().map { it.fastMap { it.toCore() } }

    suspend fun getProfile(id: Long): Profile? =
        garminDao.getProfile(id)?.toCore()

    suspend fun saveProfile(profile: Profile) =
        garminDao.saveProfile(profile.toEntity())

    suspend fun deleteProfile(profile: Profile) =
        garminDao.deleteProfile(profile.toEntity())

    suspend fun deleteTokens() {
        userDataStore.deleteOAuth1()
        userDataStore.deleteOAuth2()
    }

    suspend fun getLatestActivities(limit: Int): Result<List<Activity>> =
        callApi (
            { garminConnect.getLatestActivity(limit) },
            { res -> res.body()?.fastMap { it.toCore() } ?: emptyList() }
        )

    suspend fun getCourses(): Result<List<Course>> =
        callApi (
            { garminConnect.getCourses() },
            { res -> res.body()?.fastMap { it.toCore() } ?: emptyList() }
        )

    suspend fun getEventTypes(): Result<List<EventType>> =
        callApi (
            { garminConnect.getEventTypes() },
            { res -> res.body()?.fastMap { it.toCore() }?.filterNotNull() ?: emptyList() }
        )

    suspend fun updateActivity(
        activity: Activity,
        profile: Profile,
        effort: Float?,
        feel: Float?,
    ): Result<Unit> {
        val request = UpdateActivity(
            id = activity.id,
            name = if (profile.rename) profile.name else null ,
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