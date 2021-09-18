package uz.invan.rovitalk.util.ktx

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.audio.Player
import uz.invan.rovitalk.data.models.audio.Player.Companion.MEDIA_NOTIFICATION_CHANNEL
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.data.tools.service.PlayerService

// player-notification
fun Context.previousMediaButton(isEnabled: Boolean, text: String): NotificationCompat.Action {
    val image =
        if (isEnabled) R.drawable.ic_baseline_skip_previous_notification
        else R.drawable.ic_baseline_skip_previous_notification_not_clickable
    val intent = createPendingIntent(
        if (isEnabled) Player.Actions.PREV
        else Player.Actions.NONE
    )
    return NotificationCompat.Action(image, text, intent)
}

fun Context.controlMediaButton(
    isPlaying: Boolean,
    play: String,
    pause: String,
): NotificationCompat.Action {
    val image =
        if (isPlaying) R.drawable.ic_pause_notification
        else R.drawable.ic_play_arrow_notification
    val intent = createPendingIntent(
        if (isPlaying) Player.Actions.PAUSE
        else Player.Actions.PLAY
    )
    val text = if (isPlaying) play else pause
    return NotificationCompat.Action(image, text, intent)
}

fun Context.nextMediaButton(isEnabled: Boolean, text: String): NotificationCompat.Action {
    val image =
        if (isEnabled) R.drawable.ic_baseline_skip_next_notification
        else R.drawable.ic_baseline_skip_next_notification_not_clickable
    val intent = createPendingIntent(
        if (isEnabled) Player.Actions.NEXT
        else Player.Actions.NONE
    )
    return NotificationCompat.Action(image, text, intent)
}

fun Context.createPendingIntent(action: Player.Actions): PendingIntent? {
    val intent = Intent(this, PlayerService::class.java)
    intent.action = action.action
    return PendingIntent.getService(this, 0, intent, 0)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.buildMediaNotificationChannel() {
    val channel = NotificationChannel(
        MEDIA_NOTIFICATION_CHANNEL,
        getString(R.string.player_notification_channel_name),
        NotificationManager.IMPORTANCE_LOW
    ).apply {
        description = getString(R.string.player_notification_channel_description)
//        setSound(null, null)
    }

    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (!mediaNotificationChannelExists(manager))
        manager.createNotificationChannel(channel)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.mediaNotificationChannelExists(manager: NotificationManager) =
    manager.getNotificationChannel(MEDIA_NOTIFICATION_CHANNEL) != null

// reminder-ktx
fun String.isTime(): Boolean {
    try {
        Timber.tag("REMINDER_TIME").d("Time: $this")
        val hm = split(Credentials.TIME_DIVIDER)
        Timber.tag("REMINDER_TIME").d("hm: $hm")
        val hour = hm[0]
        val minute = hm[1]

        Timber.tag("REMINDER_TIME")
            .d("hour: $hour, length: ${hour.length}, minute: $minute, length: ${minute.length}")
        if (hour.length != Credentials.TIME_LENGTH) return false
        if (minute.length != Credentials.TIME_LENGTH) return false

        return true
    } catch (exception: Exception) {
        exception.printStackTrace()
        return false
    }
}

fun String.hour(): Int {
    try {
        val hm = split(Credentials.TIME_DIVIDER)
        val hour = hm[0]

        if (hour.length != Credentials.TIME_LENGTH) return -1

        return hour.toInt()
    } catch (exception: Exception) {
        exception.printStackTrace()
        return -1
    }
}

fun String.minutes(): Int {
    try {
        val hm = split(Credentials.TIME_DIVIDER)
        val minute = hm[1]

        if (minute.length != Credentials.TIME_LENGTH) return -1

        return minute.toInt()
    } catch (exception: Exception) {
        exception.printStackTrace()
        return -1
    }
}