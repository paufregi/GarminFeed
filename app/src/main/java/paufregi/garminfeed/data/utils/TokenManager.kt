package paufregi.garminfeed.data.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import paufregi.garminfeed.di.dataStore
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer


class TokenManager (private val context: Context) {
    companion object {
        private val OAUTH_CONSUMER = stringPreferencesKey("oauthConsumer")
        private val OAUTH1 = stringPreferencesKey("oauth1")
        private val OAUTH2 = stringPreferencesKey("oauth2")
    }

    fun getOAuthConsumer(): Flow<OAuthConsumer?> {
        return context.dataStore.data.map { preferences ->
            val consumerJson = preferences[OAUTH_CONSUMER]
            if (consumerJson != null) Json.decodeFromString<OAuthConsumer>(consumerJson) else null
        }
    }

    fun getOauth1(): Flow<OAuth1?> {
        return context.dataStore.data.map { preferences ->
            val oauthJson = preferences[OAUTH1]
            if (oauthJson != null) Json.decodeFromString<OAuth1>(oauthJson) else null
        }
    }

    fun getOauth2(): Flow<OAuth2?> {
        return context.dataStore.data.map { preferences ->
            val oauth2Json = preferences[OAUTH2]
            if (oauth2Json != null) Json.decodeFromString<OAuth2>(oauth2Json) else null
        }
    }

    suspend fun saveOAuthConsumer(oAuthConsumer: OAuthConsumer) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH_CONSUMER] = Json.encodeToString(oAuthConsumer)
        }
    }

    suspend fun saveOAuth(oAuth1: OAuth1) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH1] = Json.encodeToString(oAuth1)
        }
    }

    suspend fun saveOAuth2(oAuth2: OAuth2) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH2] = Json.encodeToString(oAuth2)
        }
    }

    suspend fun deleteOAuthConsumer() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH_CONSUMER)
        }
    }

    suspend fun deleteOAuth1() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH1)
        }
    }

    suspend fun deleteOAuth2() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH2)
        }
    }
}