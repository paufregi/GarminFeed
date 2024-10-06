package paufregi.garminfeed.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import paufregi.garminfeed.data.database.entities.CredentialEntity

@Dao
interface GarminDao {

    @Upsert
    suspend fun saveCredential(credentials: CredentialEntity)

    @Query("SELECT * FROM credentials WHERE ID = 1")
    suspend fun getCredential(): CredentialEntity?
}
