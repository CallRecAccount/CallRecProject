package uz.invan.rovitalk.util.ktx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.audio.PlayerAudio
import uz.invan.rovitalk.data.models.validation.Credentials
import java.io.File
import java.util.*
import kotlin.collections.HashMap

// media-player-ktx
class AudioPlayerTimer(
    var player: MediaPlayer,
    val onMediaUpdatedListener: OnMediaUpdatedListener? = null,
) {

    private val timer by lazy {
        object : CountDownTimer(player.duration.toLong() - player.currentPosition, 1000L) {
            override fun onTick(p0: Long) {
                onMediaUpdatedListener?.onMediaUpdated(player.currentPosition)
            }

            override fun onFinish() {}
        }
    }


    fun start() {
        timer.start()
    }

    fun cancel() {
        timer.cancel()
    }

    @FunctionalInterface
    fun interface OnMediaUpdatedListener {
        fun onMediaUpdated(millis: Int)
    }
}

@Deprecated("Deprecated, because causes player error", ReplaceWith("MediaPlayer.toPercent"))
fun MediaPlayer.percentToNumbers(percent: Int): Int {
    return duration * percent / 100
}

fun MediaPlayer.toPercent(number: Int): Int {
    // TODO: 1/16/21 Fix duration divide by zero exception
    return (number * Credentials.MAX_BUFFER_POSITION) / duration
}

private val thumbnails = hashMapOf<String, Bitmap?>()

@Suppress("RedundantSuspendModifier")
suspend fun Context.retrieveVideoThumbnail(url: String): Bitmap? {
    if (thumbnails.containsKey(url) && thumbnails[url] != null)
        return thumbnails[url]
    val bitmap: Bitmap?
    var mediaDataRetriever: MediaMetadataRetriever? = null
    try {
        mediaDataRetriever = MediaMetadataRetriever()
        mediaDataRetriever.setDataSource(url, HashMap())
        bitmap = mediaDataRetriever.frameAtTime
        thumbnails[url] = bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        return toBitmap(R.drawable.image)
//        throw Throwable("Exception in retrieveVideoThumbnail(url:String)" + e.message)
    } finally {
        mediaDataRetriever?.release()
    }

    return bitmap
}

fun RequestBuilder<Bitmap>.onBitmap(onBitmap: (Bitmap) -> Unit) {
    into(object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            onBitmap.invoke(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }

    })
}

// media session
fun MediaSessionCompat.bufferingState(player: MediaPlayer?, audio: PlayerAudio) {
    setMetadata(
        MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, audio.title)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, audio.categoryTitle)
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, audio.image)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, (player?.duration ?: -1).toLong())
            .build()
    )

    setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_BUFFERING,
                (player?.currentPosition ?: -1).toLong(),
                Credentials.PLAYER_SPEED
            )
            .setActions(PlaybackStateCompat.ACTION_PREPARE)
            .build()
    )
}

fun MediaSessionCompat.playingState(player: MediaPlayer?, audio: PlayerAudio) {
    setMetadata(
        MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, audio.title)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, audio.categoryTitle)
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, audio.image)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, (player?.duration ?: -1).toLong())
            .build()
    )

    setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PLAYING,
                (player?.currentPosition ?: -1).toLong(),
                Credentials.PLAYER_SPEED
            )
            .setActions(PlaybackStateCompat.ACTION_PLAY)
            .build()
    )
}

fun MediaSessionCompat.pausedState(player: MediaPlayer?, audio: PlayerAudio) {
    setMetadata(
        MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, audio.title)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, audio.categoryTitle)
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, audio.image)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, (player?.duration ?: -1).toLong())
            .build()
    )

    setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PAUSED,
                (player?.currentPosition ?: -1).toLong(),
                Credentials.PLAYER_SPEED
            )
            .setActions(PlaybackStateCompat.ACTION_PAUSE)
            .build()
    )
}

fun MediaSessionCompat.seekState(player: MediaPlayer?, audio: PlayerAudio) {
    setMetadata(
        MediaMetadataCompat.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, audio.title)
            .putString(MediaMetadata.METADATA_KEY_ALBUM, audio.categoryTitle)
            .putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, audio.image)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, (player?.duration ?: -1).toLong())
            .build()
    )

    setPlaybackState(
        PlaybackStateCompat.Builder()
            .setState(
                PlaybackStateCompat.STATE_PLAYING,
                (player?.currentPosition ?: -1).toLong(),
                Credentials.PLAYER_SPEED
            )
            .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
            .build()
    )
}

// file-ktx
fun File.toFormData(): MultipartBody.Part {
    val request = asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", name, request)
}

// time-ktx
fun Long.minutes(): String {
    val time = Calendar.getInstance()
    time[Calendar.HOUR_OF_DAY] = 0
    time[Calendar.MINUTE] = 0
    time[Calendar.SECOND] = 0
    time[Calendar.MILLISECOND] = this.toInt()
    return time[Calendar.MINUTE].toString().padStart(2, '0')
}

fun Int.minutes() = toLong().minutes()

fun Long.seconds(): String {
    val time = Calendar.getInstance()
    time[Calendar.HOUR_OF_DAY] = 0
    time[Calendar.MINUTE] = 0
    time[Calendar.SECOND] = 0
    time[Calendar.MILLISECOND] = this.toInt()
    return time[Calendar.SECOND].toString().padStart(2, '0')
}

fun Int.seconds() = toLong().seconds()