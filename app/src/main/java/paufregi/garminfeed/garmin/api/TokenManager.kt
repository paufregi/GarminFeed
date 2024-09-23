package paufregi.garminfeed.garmin.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import paufregi.garminfeed.garmin.api.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth1Consumer
import paufregi.garminfeed.garmin.data.Oauth2

class TokenManager (private val context: Context) {
    companion object {
        private val OAUTH1_CONSUMER_KEY = stringPreferencesKey("oauth1ConsumerKey")
        private val OAUTH1_CONSUMER_SECRET = stringPreferencesKey("oauth1ConsumerSecret")
        private val OAUTH1_TOKEN = stringPreferencesKey("oauth1Token")
        private val OAUTH1_SECRET = stringPreferencesKey("oauth1Secret")
        private val ACCESS_TOKEN = stringPreferencesKey("accessToken")
    }
    fun getOauth1Consumer(): Flow<Oauth1Consumer?> {
        return context.dataStore.data.map { preferences ->
            val key = preferences[OAUTH1_CONSUMER_KEY]
            val secret = preferences[OAUTH1_CONSUMER_SECRET]
            if(key != null && secret != null) Oauth1Consumer(key, secret) else null
        }
    }

    fun getOauth1(): Flow<Oauth1?> {
        return context.dataStore.data.map { preferences ->
            val token = preferences[OAUTH1_TOKEN]
            val secret = preferences[OAUTH1_SECRET]
            if(token != null && secret != null) Oauth1(token, secret) else null
        }
    }

    fun getOauth2(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
           preferences[ACCESS_TOKEN]
        }
    }

    suspend fun saveOauth1Consumer(oauth1Consumer: Oauth1Consumer) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH1_CONSUMER_KEY] = oauth1Consumer.key
            preferences[OAUTH1_CONSUMER_SECRET] = oauth1Consumer.secret
        }
    }

    suspend fun saveOauth1(oauth1: Oauth1) {
        context.dataStore.edit { preferences ->
            preferences[OAUTH1_TOKEN] = oauth1.token
            preferences[OAUTH1_SECRET] = oauth1.secret
        }
    }

    suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    suspend fun saveAccessToken(oauth2: Oauth2) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = oauth2.accessToken
        }
    }

    suspend fun deleteOauth1Consumer() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH1_CONSUMER_KEY)
            preferences.remove(OAUTH1_CONSUMER_SECRET)
        }
    }

    suspend fun deleteOauth1() {
        context.dataStore.edit { preferences ->
            preferences.remove(OAUTH1_TOKEN)
            preferences.remove(OAUTH1_SECRET)
        }
    }

    suspend fun deleteOauth2() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN)
        }
    }
}