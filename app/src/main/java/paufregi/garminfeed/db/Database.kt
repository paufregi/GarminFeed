package paufregi.garminfeed.db

import androidx.room.Database
import androidx.room.RoomDatabase
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Credentials

@Database(
    entities = [
        Credentials::class,
        CachedOauth1::class,
        CachedOauth2::class], version = 1
)
abstract class Database : RoomDatabase() {
    abstract val garminDao: GarminDao
}