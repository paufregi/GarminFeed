package paufregi.garminfeed

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import paufregi.garminfeed.data.database.GarminDatabase
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    @Named("test_db")
    fun provideInMemoryGarminDb(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(
            context,
            GarminDatabase::class.java
        ).allowMainThreadQueries().build()
}