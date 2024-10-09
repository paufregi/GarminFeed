package paufregi.garminfeed.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.datastore.TokenManager
import paufregi.garminfeed.presentation.ui.ShortToast
import java.io.File
import javax.inject.Singleton
import javax.inject.Named

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context = context)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        garminDao: GarminDao,
        garth: Garth,
        garminSSO: GarminSSO,
        tokenManager: TokenManager,
        @Named("GarminConnectOAuth1Url") garminConnectOAuth1Url: String,
        @Named("GarminConnectOAuth2Url") garminConnectOAuth2Url: String
    ): AuthInterceptor = AuthInterceptor(
        garminDao,
        garth,
        garminSSO,
        tokenManager,
        { oauthConsumer: OAuthConsumer -> GarminConnectOAuth1.client(oauthConsumer, garminConnectOAuth1Url) },
        { oauthConsumer: OAuthConsumer, oauth: OAuth1 -> GarminConnectOAuth2.client(oauthConsumer, oauth, garminConnectOAuth2Url) }
    )

    @Provides
    @Singleton
    fun provideGarminConnect(
        authInterceptor: AuthInterceptor,
        @Named("GarminConnectUrl") url: String
    ): GarminConnect = GarminConnect.client(authInterceptor, url)

    @Provides
    @Singleton
    fun provideGarminSSO(
        @Named("GarminSSOUrl") url: String
    ): GarminSSO = GarminSSO.client(url)

    @Provides
    @Singleton
    fun provideGarth(
        @Named("GarthUrl") url: String
    ): Garth = Garth.client(url)

    @Provides
    @Singleton
    @Named("tempFolder")
    fun provideTempFolder(@ApplicationContext context: Context): File =
        context.cacheDir

    @Provides
    @Singleton
    fun provideToast(@ApplicationContext context: Context): ShortToast = ShortToast(context)
}