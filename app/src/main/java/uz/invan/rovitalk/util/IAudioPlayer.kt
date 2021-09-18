package uz.invan.rovitalk.util

import uz.invan.rovitalk.data.models.audio.AudioData

@Deprecated("Deprecated due to new player", ReplaceWith("Player"))
interface IAudioPlayer {
    fun prepare(audio: AudioData)
    fun updateAudio(audio: AudioData)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(millis: Int)
    fun showOrUpdateNotification()
    fun hideNotification()
}