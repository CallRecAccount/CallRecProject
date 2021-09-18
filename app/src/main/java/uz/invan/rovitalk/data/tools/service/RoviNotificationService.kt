package uz.invan.rovitalk.data.tools.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color.RED
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE
import android.media.RingtoneManager
import android.media.RingtoneManager.TYPE_NOTIFICATION
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.models.notification.FileCreateNotification
import uz.invan.rovitalk.data.models.notification.OneTimeNotification
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.data.tools.service.RoviNotificationService.RoviNotificationsConstructor.constructFileCreate
import uz.invan.rovitalk.data.tools.service.RoviNotificationService.RoviNotificationsConstructor.constructOneTime
import uz.invan.rovitalk.data.tools.service.RoviNotificationService.RoviNotificationsConstructor.planMoreTimeNotification
import uz.invan.rovitalk.data.tools.workers.ReminderWorker
import uz.invan.rovitalk.ui.activity.MainActivity
import uz.invan.rovitalk.util.ktx.coroutineIO
import uz.invan.rovitalk.util.ktx.hour
import uz.invan.rovitalk.util.ktx.minutes
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class RoviNotificationService : FirebaseMessagingService() {
    @Inject
    lateinit var reminder: ReminderRepository

    @Inject
    lateinit var prefs: RoviPrefs

    override fun onNewToken(token: String) {
        // save new token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Timber.d("message: ${remoteMessage.data}")
        // checks by notification type and then creates notification
        when (remoteMessage.data[NOTIFICATION_TYPE]) {
            RoviNotification.FILE_CREATE.topic -> {
                val fileCreate = remoteMessage.data.toRoviData(FileCreateNotification::class.java)
                constructFileCreate(fileCreate)
            }
            RoviNotification.REMINDER_NOTIFICATION.topic -> {
                coroutineIO {
                    reminder.retrieveReminderNotifications()
                        .catch { exception -> Timber.d(exception) }
                        .collect { reminder -> planMoreTimeNotification(reminder) }
                }
            }
            RoviNotification.ONE_TIME.topic -> {
                val oneTime = remoteMessage.data.toRoviData(OneTimeNotification::class.java)
                val language = RoviLanguage.values().firstOrNull { it.lang == prefs.language }
                    ?: RoviLanguage.UZ
                constructOneTime(oneTime, language)
            }
        }
    }

    private object RoviNotificationsConstructor {
        fun Context.constructFileCreate(fileCreate: FileCreateNotification) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val (title, author, _) = fileCreate

            val titleText = getString(R.string.new_file_created)
            val contentText = getString(R.string.new_file_content, title, author)

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_FILE_CREATE)
                .setSmallIcon(R.drawable.ic_baseline_fiber_new_24)
                .setContentTitle(titleText)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.victoria))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle())

            notification.priority = PRIORITY_MAX

            // create notification channel
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification.setChannelId(NOTIFICATION_CHANNEL_FILE_CREATE)

                val ringtoneManager = RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
                val audioAttributes =
                    AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(CONTENT_TYPE_SONIFICATION).build()

                val channelName = getString(R.string.new_file_channel)
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_FILE_CREATE,
                    channelName,
                    IMPORTANCE_HIGH
                )

                channel.enableLights(true)
                channel.lightColor = RED
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                channel.setSound(ringtoneManager, audioAttributes)
                nm.createNotificationChannel(channel)
            }

            nm.notify(Random().nextInt(), notification.build())
        }

        fun Context.constructOneTime(oneTime: OneTimeNotification, lang: RoviLanguage) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            val title = when (lang) {
                RoviLanguage.UZ -> oneTime.titleObject.uz
                RoviLanguage.RU -> oneTime.titleObject.ru
                RoviLanguage.EN -> oneTime.titleObject.en
            }
            val content = when (lang) {
                RoviLanguage.UZ -> oneTime.contentObject.uz
                RoviLanguage.RU -> oneTime.contentObject.ru
                RoviLanguage.EN -> oneTime.contentObject.en
            }

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val notification = NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_FILE_CREATE)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.shark))
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle())

            notification.priority = PRIORITY_MAX

            // create notification channel
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification.setChannelId(NOTIFICATION_CHANNEL_FILE_CREATE)

                val ringtoneManager =
                    RingtoneManager.getDefaultUri(TYPE_NOTIFICATION)
                val audioAttributes =
                    AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                        .setContentType(CONTENT_TYPE_SONIFICATION).build()

                val channelName = getString(R.string.daily_reminder_channel)
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_FILE_CREATE, channelName, IMPORTANCE_HIGH
                )

                channel.enableLights(true)
                channel.lightColor = RED
                channel.enableVibration(true)
                channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                channel.setSound(ringtoneManager, audioAttributes)
                nm.createNotificationChannel(channel)
            }

            nm.notify(Random().nextInt(), notification.build())
        }

        fun Context.planMoreTimeNotification(reminder: ReminderEntity) {
            val now = Calendar.getInstance()
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, reminder.time.hour())
            calendar.set(Calendar.MINUTE, reminder.time.minutes())
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(now))
                calendar.add(Calendar.HOUR_OF_DAY, 24)

            val timeDiff = calendar.timeInMillis - now.timeInMillis
            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag(reminder.id)
                .setInputData(workDataOf(ReminderWorker.REMINDER_ID to reminder.id))
                .build()

            WorkManager.getInstance(this)
                .cancelAllWorkByTag(reminder.id)
            WorkManager.getInstance(this).enqueueUniqueWork(
                reminder.id,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
            Timber.d("Added: $reminder")
        }
    }

    private fun <T> Map<String, String>.toRoviData(classOfT: Class<T>): T {
        val json = this[NOTIFICATION_DATA]
        return Gson().fromJson(json, classOfT)
    }

    companion object {
        const val NOTIFICATION_TYPE = "type"
        const val NOTIFICATION_DATA = "data"

        // channels
        const val NOTIFICATION_CHANNEL_FILE_CREATE =
            "uz.invan.rovitalk.data.tools.service.RoviNotificationService.notification_channel_file_create"
    }
}

enum class RoviNotification(val topic: String) {
    FILE_CREATE("new_file"),
    REMINDER_NOTIFICATION("notification"),
    ONE_TIME("one_time")
}