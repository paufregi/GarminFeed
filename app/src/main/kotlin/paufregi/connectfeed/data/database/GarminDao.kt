package paufregi.connectfeed.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity

@Dao
interface GarminDao {

    @Upsert
    suspend fun saveCredential(credential: CredentialEntity)

    @Query("SELECT * FROM credentials WHERE ID = 1")
    fun getCredential(): Flow<CredentialEntity?>

    @Upsert
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE ID = :id")
    fun getProfile(id: Int): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles ORDER BY activityTypeId, name")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

}
