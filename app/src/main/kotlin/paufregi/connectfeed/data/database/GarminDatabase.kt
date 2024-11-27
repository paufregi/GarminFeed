package paufregi.connectfeed.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import paufregi.connectfeed.data.database.coverters.ActivityTypeConverter
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity

@Database(entities = [CredentialEntity::class, ProfileEntity::class], version = 2)
@TypeConverters(ActivityTypeConverter::class)
abstract class GarminDatabase : RoomDatabase() {
    abstract fun garminDao(): GarminDao
}