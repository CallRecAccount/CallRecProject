@file:Suppress("DEPRECATION")

package uz.invan.rovitalk.data.models.audio

import androidx.annotation.IntRange
import java.io.File

@Deprecated("Deprecated due to new player", ReplaceWith("PlayerAudio"))
data class AudioData(
    val id: Int,
    val url: String,
    val album: String,
    val audioTitle: String,
    var backgroundMusic: BackgroundAudioData?,
    var updateReason: AudioUpdateReasons? = null,
)

@Deprecated("Deprecated due to new player", ReplaceWith("PlayerBM"))
data class BackgroundAudioData(
    val url: String?,
    @IntRange(from = 0, to = 100) var volumePercent: Int, // in percent
    val prev: BackgroundAudioData?,
    val next: BackgroundAudioData?,
)

@Deprecated("Deprecated due to new player")
enum class AudioUpdateReasons {
    VOLUME_UPDATE, BACKGROUND_AUDIO_UPDATE, BACKGROUND_AUDIO_STOP
}

data class PlayerAudio(
    val id: String,
    val title: String,
    val image: String,
    val duration: Long,
    val category: String,
    val categoryTitle: String,
    val author: String,
    val categoryImage: String?,
    val url: String?,
    var lastPlayedPosition: Int? = null,
    var bufferedPosition: Int? = null,
    val file: File? = null,
    val createdAt: Long,
    val updatedAt: Long,
)

data class PlayerBM(
    val id: String,
    val image: String,
    val state: BMState,
    val file: File? = null,
    var visibility: BMVisibility = BMVisibility.VISIBLE,
)

enum class BMState {
    LOADING, LOADED
}

enum class BMVisibility {
    VISIBLE, INVISIBLE
}

val fakePlayerBM = PlayerBM("", "", BMState.LOADED)