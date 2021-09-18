package uz.invan.rovitalk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.data.local.database.RoviDatabase
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.local.prefs.RoviPrefsImpl
import uz.invan.rovitalk.data.models.validation.NetworkResults
import uz.invan.rovitalk.data.remote.RoviApi
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.data.repository.ReminderRepository
import uz.invan.rovitalk.data.repository.ReminderRepositoryImpl
import uz.invan.rovitalk.util.files.RoviFiles
import uz.invan.rovitalk.util.files.RoviFilesImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Singleton
    @Provides
    fun provideContext(app: Application) = app.applicationContext!!

    @Singleton
    @Provides
    fun provideChuckerCollector(app: Application): ChuckerCollector = ChuckerCollector(
        context = app,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.FOREVER
    )

    @Singleton
    @Provides
    fun provideChuckerInterceptor(
        app: Application,
        collector: ChuckerCollector,
    ): ChuckerInterceptor =
        ChuckerInterceptor.Builder(app)
            .collector(collector)
            .redactHeaders(setOf("Authorization"))
            .build()

    @Singleton
    @Provides
    fun provideRoviInterceptor(prefs: RoviPrefs): Interceptor = Interceptor { chain ->
        chain.run {
            val response = proceed(
                request().newBuilder()
                    .addHeader("Authorization", prefs.profile?.token ?: "")
                    .addHeader("role", BuildConfig.ROLE)
                    .addHeader("language", prefs.language)
                    .addHeader("version", BuildConfig.VERSION_NAME)
                    .build()
            )
            if (response.code == NetworkResults.UNAUTHORIZED) {
                prefs.clear()
            }
            response
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        chuckerInterceptor: ChuckerInterceptor,
        interceptor: Interceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(chuckerInterceptor)
        .addInterceptor(interceptor)
        .build()

    @Singleton
    @Provides
    fun provideConverterFactor(): Converter.Factory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, factory: Converter.Factory): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.ROVI_URL)
            .client(client)
            .addConverterFactory(factory)
            .build()

    @Singleton
    @Provides
    fun provideRoviApi(retrofit: Retrofit): RoviApi = retrofit.create(RoviApi::class.java)

    @Singleton
    @Provides
    fun provideApiHelper(api: RoviApi, collector: ChuckerCollector): RoviApiHelper =
        RoviApiHelper(api = api, collector = collector)

    @Singleton
    @Provides
    fun provideRoviDatabase(app: Application): RoviDatabase =
        Room.databaseBuilder(app, RoviDatabase::class.java, BuildConfig.ROVI_DATABASE).build()

    @Singleton
    @Provides
    fun provePreferences(): RoviPrefs = RoviPrefsImpl

    @Singleton
    @Provides
    fun provideRoviFiles(context: Context): RoviFiles = RoviFilesImpl.initialize(context)

    @Singleton
    @Provides
    fun provideReminderRepository(
        apiHelper: RoviApiHelper,
        database: RoviDatabase,
        prefs: RoviPrefs,
    ): ReminderRepository = ReminderRepositoryImpl(
        apiHelper = apiHelper,
        reminderDao = database.reminderDao(),
        prefs = prefs
    )
}