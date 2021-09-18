package uz.invan.rovitalk.data.models.audio

interface PlayerItemListener {
    fun image(image: String)
    fun title(title: String)
    fun author(author: String)
    fun load()
    fun ready(duration: Int)
    fun play()
    fun pause()
    fun seek(position: Int)
    fun loaderSeek(position: Int)
    fun release()
}