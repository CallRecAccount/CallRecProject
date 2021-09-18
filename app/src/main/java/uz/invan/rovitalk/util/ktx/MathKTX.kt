package uz.invan.rovitalk.util.ktx

import android.media.MediaPlayer
import uz.invan.rovitalk.data.models.validation.Credentials

// time-ktx
fun Int.roundMinutes(): Int {
    return this / 1000 / 60
}

// media-ktx
fun MediaPlayer.setRoviVolume(volumePercent: Int) {
    val volume = volumePercent.toFloat() / Credentials.MAX_VOLUME
    setVolume(volume, volume)
}