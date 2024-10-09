package paufregi.garminfeed.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
class TestDatabaseModule {
    @Provides
    @Singleton
    fun provideGarminDatabase(@ApplicationContext context: Context): GarminDatabase =
        Room.inMemoryDatabaseBuilder(context, GarminDatabase::class.java).build()

    @Provides
    @Singleton
    fun provideGarminDao(garminDatabase: GarminDatabase): GarminDao =
        garminDatabase.garminDao()
}