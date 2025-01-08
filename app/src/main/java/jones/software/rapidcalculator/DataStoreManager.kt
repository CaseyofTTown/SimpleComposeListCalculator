package jones.software.rapidcalculator

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    // Create the DataStore instance
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("app_preferences")
        val BACKGROUND_IMAGE_URI_KEY = stringPreferencesKey("background_image_uri")
    }

    // Save the background image URI
    suspend fun saveBackgroundImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[BACKGROUND_IMAGE_URI_KEY] = uri
        }
    }

    // Get the background image URI
    fun getBackgroundImageUri(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[BACKGROUND_IMAGE_URI_KEY]
        }
    }
}