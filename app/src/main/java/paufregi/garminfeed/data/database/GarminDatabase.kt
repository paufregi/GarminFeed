package paufregi.garminfeed.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dagger.Provides
import paufregi.garminfeed.data.database.models.Credentials

@Database(entities = [Credentials::class], version = 1)
abstract class GarminDatabase : RoomDatabase() {
    abstract fun garminDao(): GarminDao
}