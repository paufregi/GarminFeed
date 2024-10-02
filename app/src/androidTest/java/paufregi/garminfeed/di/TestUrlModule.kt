package paufregi.garminfeed.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import paufregi.garminfeed.connectOAuth1Port
import paufregi.garminfeed.connectOAuth2Port
import paufregi.garminfeed.connectPort
import paufregi.garminfeed.garminSSOPort
import paufregi.garminfeed.garthPort
import javax.inject.Singleton
import javax.inject.Named

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UrlModule::class]
)
class TestUrlModule {
    @Provides
    @Singleton
    @Named("GarminConnectUrl")
    fun provideGarminConnectUrl(): String = "https://localhost:${connectPort}/"

    @Provides
    @Singleton
    @Named("GarminConnectOAuth1Url")
    fun provideGarminConnectOAuth1Url(): String = "https://localhost:${connectOAuth1Port}/"

    @Provides
    @Singleton
    @Named("GarminConnectOAuth2Url")
    fun provideGarminConnectOAuth2Url(): String = "https://localhost:${connectOAuth2Port}/"

    @Provides
    @Singleton
    @Named("GarminSSOUrl")
    fun provideGarminSSOUrl(): String = "https://localhost:${garminSSOPort}/"

    @Provides
    @Singleton
    @Named("GarthUrl")
    fun provideGarthUrl(): String = "https://localhost:${garthPort}/"
}