package uz.invan.rovitalk.data.repository

import kotlinx.coroutines.CoroutineScope
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import javax.inject.Inject

interface MainRepository {

    /**
     * Disables current audio
     * Must call in [CoroutineScope]
     * */
    suspend fun disableCurrentAudio()

    /**
     * Clears temporary configuration data from preferences
     * Must call in [CoroutineScope]
     * */
    suspend fun clearTemporaryConfigurations()

    /**
     * Saves temporary qr
     * @param qr qr which will be saved in prefs
     * */
    fun saveQR(qr: String)
}

class MainRepositoryImpl @Inject constructor(
    private val prefs: RoviPrefs,
) : MainRepository {
    override suspend fun disableCurrentAudio() {
        prefs.currentAudio = null
    }

    override suspend fun clearTemporaryConfigurations() {
        prefs.configuration = prefs.configuration?.copy(
            sectionsAndCategoriesLastUpdatedTime = null
        )
    }

    override fun saveQR(qr: String) {
        prefs.qr = qr
    }
}