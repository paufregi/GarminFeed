package paufregi.garminfeed.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.api.GarminConnectOAuth1
import paufregi.garminfeed.data.api.GarminConnectOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import javax.inject.Singleton
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class UrlModule {

    @Provides
    @Singleton
    @Named("GarminConnectUrl")
    fun provideGarminConnectUrl(): String = GarminConnect.BASE_URL

    @Provides
    @Singleton
    @Named("GarminConnectOAuth1Url")
    fun provideGarminConnectOAuth1Url(): String = GarminConnectOAuth1.BASE_URL

    @Provides
    @Singleton
    @Named("GarminConnectOAuth2Url")
    fun provideGarminConnectOAuth2Url(): String = GarminConnectOAuth2.BASE_URL

    @Provides
    @Singleton
    @Named("GarminSSOUrl")
    fun provideGarminSSOUrl(): String = GarminSSO.BASE_URL

    @Provides
    @Singleton
    @Named("GarthUrl")
    fun provideGarthUrl(): String = Garth.BASE_URL
}