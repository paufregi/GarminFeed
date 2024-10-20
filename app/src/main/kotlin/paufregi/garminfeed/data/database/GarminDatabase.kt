package paufregi.garminfeed.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import paufregi.garminfeed.data.database.entities.CredentialEntity

@Database(entities = [CredentialEntity::class], version = 1)
abstract class GarminDatabase : RoomDatabase() {
    abstract fun garminDao(): GarminDao
}