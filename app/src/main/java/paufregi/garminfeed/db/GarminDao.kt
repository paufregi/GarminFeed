package paufregi.garminfeed.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Credentials

@Dao
interface GarminDao {

    @Upsert
    suspend fun upsertCredentials(credentials: Credentials)

    @Upsert
    suspend fun upsertCachedOauth1(oauth1: CachedOauth1)

    @Upsert
    suspend fun upsertCachedOauth2(oauth2: CachedOauth2)

    @Delete
    suspend fun deleteOauth1(oauth1: CachedOauth1)

    @Delete
    suspend fun deleteOauth2(oauth2: CachedOauth2)

    @Query("SELECT * FROM credentials WHERE ID = 1")
    fun getCredentials(): Flow<Credentials>

    @Query("SELECT * FROM cachedOauth1 WHERE ID = 1")
    fun getCachedOauth1(): Flow<CachedOauth1?>

    @Query("SELECT * FROM cachedOauth2 WHERE ID = 1")
    fun getCachedOauth2(): Flow<CachedOauth2?>
}
