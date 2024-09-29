package paufregi.garminfeed.di

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
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.api.GarminConnectOAuth1
import paufregi.garminfeed.data.api.GarminConnectOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.repository.GarminAuthRepository
import paufregi.garminfeed.data.datastore.TokenManager
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideGarminConnectOAuth1Url(): String = GarminConnectOAuth1.BASE_URL

    @Provides
    @Singleton
    fun provideGarminConnectOAuth2Url(): String = GarminConnectOAuth2.BASE_URL

    @Provides
    @Singleton
    fun provideGarminDatabase(@ApplicationContext context: Context): GarminDatabase =
        Room.databaseBuilder(context, GarminDatabase::class.java, "garminFeed").build()

    @Provides
    @Singleton
    fun provideGarminDao(garminDatabase: GarminDatabase): GarminDao =
        garminDatabase.garminDao()

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context = context)

    @Provides
    @Singleton
    fun provideGarth(): Garth =
        Garth.client(Garth.BASE_URL)

    @Provides
    @Singleton
    fun provideGarminSSO(): GarminSSO =
        GarminSSO.client(Garth.BASE_URL)

    @Provides
    @Singleton
    fun provideGarminAuthRepo(
        garminDao: GarminDao,
        garminSSO: GarminSSO,
        garth: Garth,
        tokenManager: TokenManager,
    ): GarminAuthRepository =
        GarminAuthRepository(
            garminDao,
            garminSSO,
            garth,
            tokenManager,
            provideGarminConnectOAuth1Url(),
            provideGarminConnectOAuth2Url()
        )

    @Provides
    @Singleton
    fun provideGarminConnect(
        authRepo: GarminAuthRepository,
        tokenManager: TokenManager
    ): GarminConnect =
        GarminConnect.client(authRepo, tokenManager, GarminConnect.BASE_URL)

}