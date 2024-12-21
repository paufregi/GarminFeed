package paufregi.connectfeed.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import paufregi.connectfeed.data.database.entities.ProfileEntity

@Dao
interface GarminDao {

    @Upsert
    suspend fun saveProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles ORDER BY activityType, name")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE ID = :id")
    suspend fun getProfile(id: Long): ProfileEntity?
}
