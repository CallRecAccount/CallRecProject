package uz.invan.rovitalk

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import uz.invan.rovitalk.data.local.prefs.RoviPrefsImpl
import uz.invan.rovitalk.data.tools.service.RoviNotification
import javax.inject.Inject

@HiltAndroidApp
class Rovi : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // setup project initial settings
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        RoviPrefsImpl.init(this)
        // subscribing to topics
        Firebase.messaging.subscribeToTopic(RoviNotification.FILE_CREATE.topic)
        Firebase.messaging.subscribeToTopic(RoviNotification.REMINDER_NOTIFICATION.topic)
        Firebase.messaging.subscribeToTopic(RoviNotification.ONE_TIME.topic)
    }

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}