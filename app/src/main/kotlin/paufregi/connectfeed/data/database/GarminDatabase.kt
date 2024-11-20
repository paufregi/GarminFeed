package paufregi.connectfeed.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity

@Database(entities = [CredentialEntity::class, ProfileEntity::class], version = 2)
abstract class GarminDatabase : RoomDatabase() {
    abstract fun garminDao(): GarminDao
}