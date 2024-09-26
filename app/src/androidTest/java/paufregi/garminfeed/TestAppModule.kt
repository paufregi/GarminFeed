package paufregi.garminfeed

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.datastore.TokenManager
import javax.inject.Named
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "test_data_store")

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

    @Provides
    @Singleton
    @Named("test_token_manager")
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context = context)
}