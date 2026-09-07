package com.anant.sonqiva.data.model

import android.net.Uri
import java.util.Locale

data class Song(
    val id: Long,
    val mediaUri: Uri? = null,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val albumArtUri: Uri? = null,
    val durationMs: Long = 0L,
    val folderPath: String = "",
    val relativePath: String = "",
    val dateAdded: Long = 0L,
    val trackNumber: Int = 0,
    val sizeBytes: Long = 0L,
    val mimeType: String = "audio/mpeg",
    val isFavorite: Boolean = false,
    val playCount: Int = 0
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return if (minutes >= 60) {
                val hours = minutes / 60
                val remMinutes = minutes % 60
                String.format(Locale.US, "%d:%02d:%02d", hours, remMinutes, seconds)
            } else {
                String.format(Locale.US, "%d:%02d", minutes, seconds)
            }
        }
}
