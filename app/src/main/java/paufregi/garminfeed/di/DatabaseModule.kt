package paufregi.garminfeed.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideGarminDatabase(@ApplicationContext context: Context): GarminDatabase =
        Room.databaseBuilder(context, GarminDatabase::class.java, "garminFeed").build()

    @Provides
    @Singleton
    fun provideGarminDao(database: GarminDatabase): GarminDao =
        database.garminDao()
}