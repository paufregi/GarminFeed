package paufregi.garminfeed.garmin.api

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.compose.runtime.key
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import paufregi.garminfeed.garmin.api.Garth.Companion.BASE_URL
import paufregi.garminfeed.garmin.api.repository.GarminSignInRepo
import paufregi.garminfeed.garmin.api.repository.GarthRepo
import paufregi.garminfeed.garmin.api.repository.SharedPreferencesKeys
import paufregi.garminfeed.garmin.converters.GarminConverterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    @Provides
    @Singleton
    fun provideGarth(): Garth {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Garth::class.java)
    }

    @Provides
    @Singleton
    fun provideGarthRepo(garth: Garth): GarthRepo {
        return GarthRepo(garth)
    }

    @Provides
    @Singleton
    fun provideGarminSignIn(): GarminSSO {
        return Retrofit.Builder()
            .baseUrl(GarminSSO.BASE_URL)
            .addConverterFactory(GarminConverterFactory())
            .build()
            .create(GarminSSO::class.java)
    }

    @Provides
    @Singleton
    fun provideGarminSignInRepo(garminSSO: GarminSSO): GarminSignInRepo {
        return GarminSignInRepo(garminSSO)
    }

    @Provides
    @Singleton
    fun provideGarminConnectOauth1(prefs: SharedPreferences): GarminConnectOauth1 {
        val key = prefs.getString(SharedPreferencesKeys.consumer_key, "")
        val secret = prefs.getString(SharedPreferencesKeys.consumer_secret, "")

        val consumer = OkHttpOAuthConsumer(key, secret)

        val client = OkHttpClient.Builder()
            .addInterceptor(SigningInterceptor(consumer))

        return Retrofit.Builder()
            .baseUrl(GarminConnectOauth1.BASE_URL)
            .addConverterFactory(GarminConverterFactory())
            .client(client.build())
            .build()
            .create(GarminConnectOauth1::class.java)
    }
}