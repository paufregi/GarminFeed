package paufregi.garminfeed.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import paufregi.garminfeed.data.database.models.Credentials

@Dao
interface GarminDao {

    @Upsert
    suspend fun saveCredentials(credentials: Credentials)

    @Query("SELECT * FROM credentials WHERE ID = 1")
    suspend fun getCredentials(): Credentials?
}
