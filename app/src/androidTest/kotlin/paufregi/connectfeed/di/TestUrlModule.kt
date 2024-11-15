package paufregi.connectfeed.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import paufregi.connectfeed.connectPort
import paufregi.connectfeed.garminSSOPort
import paufregi.connectfeed.garthPort
import javax.inject.Named
import javax.inject.Singleton

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
    fun provideGarminConnectOAuth1Url(): String = "https://localhost:${connectPort}/"

    @Provides
    @Singleton
    @Named("GarminConnectOAuth2Url")
    fun provideGarminConnectOAuth2Url(): String = "https://localhost:${connectPort}/"

    @Provides
    @Singleton
    @Named("GarminSSOUrl")
    fun provideGarminSSOUrl(): String = "https://localhost:${garminSSOPort}/"

    @Provides
    @Singleton
    @Named("GarthUrl")
    fun provideGarthUrl(): String = "https://localhost:${garthPort}/"
}