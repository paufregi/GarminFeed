package paufregi.garminfeed.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import paufregi.garminfeed.data.database.entities.CredentialEntity

@Dao
interface GarminDao {

    @Upsert
    suspend fun saveCredential(credentials: CredentialEntity)

    @Query("SELECT * FROM credentials WHERE ID = 1")
    fun getCredential(): Flow<CredentialEntity?>

}
