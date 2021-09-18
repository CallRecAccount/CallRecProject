package uz.invan.rovitalk.data.tools.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.entities.ReminderEntity
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.data.tools.service.RoviNotificationService
import uz.invan.rovitalk.ui.activity.MainActivity
import uz.invan.rovitalk.util.ktx.hour
import uz.invan.rovitalk.util.ktx.minutes
import java.util.*
import java.util.concurrent.TimeUnit

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val reminderRepository: ReminderRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Timber.tag("REMINDER_WORKER").d("Trying to work, ${inputData.getString(REMINDER_ID)}")
        val id = inputData.getString(REMINDER_ID) ?: return Result.failure()
        val reminder = reminderRepository.retrieveReminder(id) ?: return Result.failure()
        val isRemindingEnabled = reminderRepository.isReminderEnabled()

        Timber.tag("REMINDER_WORKER").d("Reminder: $reminder")
        if (isRemindingEnabled or !reminder.isPrivate)
            notify(reminder)

        // reminds again to next day
        remind(reminder)

        return Result.success()
    }

    private fun notify(reminder: ReminderEntity) {
        val nm = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(
            appContext,
            RoviNotificationService.NOTIFICATION_CHANNEL_FILE_CREATE
        )
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(reminder.title)
            .setContentText(reminder.content)
            .setContentIntent(pendingIntent)
            .setColor(ContextCompat.getColor(appContext, R.color.shark))
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle())

        notification.priority = NotificationCompat.PRIORITY_MAX

        // create notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification.setChannelId(RoviNotificationService.NOTIFICATION_CHANNEL_FILE_CREATE)

            val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes =
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()

            val channelName = appContext.getString(R.string.daily_reminder_channel)
            val channel = NotificationChannel(
                RoviNotificationService.NOTIFICATION_CHANNEL_FILE_CREATE,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setSound(ringtoneManager, audioAttributes)
            nm.createNotificationChannel(channel)
        }

        nm.notify(Random().nextInt(), notification.build())
        Timber.tag("REMINDER_WORKER").d("Notified")
    }

    private fun remind(reminder: ReminderEntity) {
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, reminder.time.hour())
        calendar.set(Calendar.MINUTE, reminder.time.minutes())

        calendar.set(Calendar.SECOND, calendar[Calendar.SECOND] - 1)
        if (calendar.before(now))
            calendar.add(Calendar.HOUR_OF_DAY, 24)

        val timeDiff = calendar.timeInMillis - now.timeInMillis
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag(reminder.id)
            .setInputData(workDataOf(REMINDER_ID to reminder.id))
            .build()

        WorkManager.getInstance(appContext).cancelAllWorkByTag(reminder.id)
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            reminder.id,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    companion object {
        const val REMINDER_ID = "uz.invan.rovitalk.data.workersReminderWorker.reminder_id"
    }
}