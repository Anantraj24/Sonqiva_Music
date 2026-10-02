package com.anant.sonqiva.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anant.sonqiva.data.model.AlbumSortOrder
import com.anant.sonqiva.data.model.SongSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sonqiva_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val LOW_MEMORY_MODE = booleanPreferencesKey("low_memory_mode")
        val PLAYBACK_SPEED = floatPreferencesKey("playback_speed")
        val AUTO_RESUME = booleanPreferencesKey("auto_resume")
        val LAST_PLAYED_SONG_ID = longPreferencesKey("last_played_song_id")
        val LAST_PLAYED_POSITION = longPreferencesKey("last_played_position")
        val SONG_SORT_ORDER = stringPreferencesKey("song_sort_order")
        val ALBUM_SORT_ORDER = stringPreferencesKey("album_sort_order")
        val EXCLUDED_FOLDER_PATHS = stringSetPreferencesKey("excluded_folder_paths")
    }

    val lowMemoryModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LOW_MEMORY_MODE] ?: false
    }

    val playbackSpeedFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PLAYBACK_SPEED] ?: 1.0f
    }

    val autoResumeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_RESUME] ?: true
    }

    val lastPlayedSongIdFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_PLAYED_SONG_ID] ?: -1L
    }

    val lastPlayedPositionFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_PLAYED_POSITION] ?: 0L
    }

    val songSortOrderFlow: Flow<SongSortOrder> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.SONG_SORT_ORDER]
        if (name != null) {
            try {
                SongSortOrder.valueOf(name)
            } catch (e: Exception) {
                SongSortOrder.TITLE_ASC
            }
        } else {
            SongSortOrder.TITLE_ASC
        }
    }

    val albumSortOrderFlow: Flow<AlbumSortOrder> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.ALBUM_SORT_ORDER]
        if (name != null) {
            try {
                AlbumSortOrder.valueOf(name)
            } catch (e: Exception) {
                AlbumSortOrder.TITLE_ASC
            }
        } else {
            AlbumSortOrder.TITLE_ASC
        }
    }

    val excludedFolderPathsFlow: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.EXCLUDED_FOLDER_PATHS] ?: emptySet()
    }

    suspend fun setExcludedFolderPaths(paths: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EXCLUDED_FOLDER_PATHS] = paths
        }
    }

    suspend fun setLowMemoryMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOW_MEMORY_MODE] = enabled
        }
    }

    suspend fun setPlaybackSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PLAYBACK_SPEED] = speed
        }
    }

    suspend fun setAutoResume(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_RESUME] = enabled
        }
    }

    suspend fun setSongSortOrder(sortOrder: SongSortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SONG_SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun setAlbumSortOrder(sortOrder: AlbumSortOrder) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ALBUM_SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun saveLastPlaybackState(songId: Long, positionMs: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_PLAYED_SONG_ID] = songId
            preferences[PreferencesKeys.LAST_PLAYED_POSITION] = positionMs
        }
    }
}
