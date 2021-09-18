package uz.invan.rovitalk.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import uz.invan.rovitalk.data.local.database.RoviDatabase
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.data.repository.*
import uz.invan.rovitalk.util.files.RoviFiles

@Module
@InstallIn(SingletonComponent::class)
class ActivityModule {
    @Provides
    fun provideAuthRepository(
        apiHelper: RoviApiHelper,
        database: RoviDatabase,
        prefs: RoviPrefs,
    ): AuthRepository =
        AuthRepositoryImpl(
            apiHelper = apiHelper,
            database = database,
            prefs = prefs,
            resendIntervalInMinutes = Credentials.RESEND_ATTEMPTS_INTERVAL_IN_MINUTES,
            phoneFormatMask = Credentials.PHONE_FORMAT_MASK
        )

    @Provides
    fun providePodcastsRepository(
        apiHelper: RoviApiHelper,
        database: RoviDatabase,
        roviFiles: RoviFiles,
        prefs: RoviPrefs,
    ): PodcastsRepository =
        PodcastsRepositoryImpl(
            apiHelper = apiHelper,
            sectionsAndCategoriesDao = database.sectionsAndCategoriesDao(),
            bmDao = database.bmDao(),
            audioDao = database.audioDao(),
            priceDao = database.priceDao(),
            roviFiles = roviFiles,
            prefs = prefs,
            minSectionsUpdateIntervalInMinutes = Credentials.MIN_SECTIONS_UPDATE_INTERVAL_IN_MINUTES
        )

    @Provides
    fun provideMainRepository(prefs: RoviPrefs): MainRepository = MainRepositoryImpl(prefs = prefs)
}