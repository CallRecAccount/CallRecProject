package uz.invan.rovitalk.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.Rovi
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_CURRENT_AUDIO
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_INTRODUCTION
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_LANGUAGE
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_PROFILE
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_QR
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_ROVI_CONFIGURATION
import uz.invan.rovitalk.data.local.prefs.RoviPrefs.Companion.KEY_SETTINGS_LINKS
import uz.invan.rovitalk.data.models.audio.AudioData
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.settings.SettingsLinks
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.data.models.validation.RoviConfiguration

interface RoviPrefs {
    /**
     * Current playing audio, null if removed from player-footer
     * */
    var currentAudio: FeedCategory?

    /**
     * Profile of authenticated user
     * */
    var profile: Profile?

    /**
     * Rovi configuration data stored
     * */
    var configuration: RoviConfiguration?

    /**
     * Rovi introduction video
     * */
    var introduction: String?

    /**
     * Rovi settings links
     * */
    var settingsLinks: SettingsLinks?

    /**
     * Rovi language
     * */
    var language: String

    /**
     * Temporary qr fetched from link
     * */
    var qr: String?

    fun clear()

    companion object {
        // keys for loading preferences
        const val KEY_CURRENT_AUDIO = "uz.invan.rovitalk.data.prefs.RoviPrefs.currentAudio"
        const val KEY_PROFILE = "uz.invan.rovitalk.data.prefs.RoviPrefs.profile"
        const val KEY_ROVI_CONFIGURATION =
            "uz.invan.rovitalk.data.prefs.RoviPrefs.roviConfiguration"
        const val KEY_INTRODUCTION = "uz.invan.rovitalk.data.prefs.RoviPrefs.introduction"
        const val KEY_SETTINGS_LINKS = "uz.invan.rovitalk.data.prefs.RoviPrefs.settingsLinks"
        const val KEY_LANGUAGE = "uz.invan.rovitalk.data.prefs.RoviPrefs.language"
        const val KEY_QR = "uz.invan.rovitalk.data.prefs.RoviPrefs.qr"
    }
}

object RoviPrefsImpl : RoviPrefs {
    private lateinit var prefs: SharedPreferences

    /**
     * Used for creating instance of [SharedPreferences]
     * It should be instantiated in [Rovi] to prevent exception
     * @param context - instance of [Context], which uses method getSharedPreferences()
     * */
    fun init(context: Context) {
        if (!RoviPrefsImpl::prefs.isInitialized) {
            prefs = context.getSharedPreferences(BuildConfig.ROVI_PREFERENCES, Context.MODE_PRIVATE)
        }
    }

    /**
     * Gets instance of [AudioData], converts to json and saves it to [SharedPreferences]
     * @return audio which is lastly played and not removed from player-footer
     * @throws [UninitializedPropertyAccessException] [RoviPrefsImpl.prefs] not initialized
     * */
    override var currentAudio: FeedCategory?
        set(value) {
            prefs.edit { putString(KEY_CURRENT_AUDIO, Gson().toJson(value)) }
        }
        get() {
            val jsonCurrentAudio = prefs.getString(KEY_CURRENT_AUDIO, null)
            return Gson().fromJson(jsonCurrentAudio, FeedCategory::class.java)
        }

    override var profile: Profile?
        set(value) {
            prefs.edit { putString(KEY_PROFILE, Gson().toJson(value)) }
        }
        get() {
            val jsonProfile = prefs.getString(KEY_PROFILE, null)
            return Gson().fromJson(jsonProfile, Profile::class.java)
        }

    override var configuration: RoviConfiguration?
        set(value) {
            prefs.edit { putString(KEY_ROVI_CONFIGURATION, Gson().toJson(value)) }
        }
        get() {
            val jsonConfiguration = prefs.getString(KEY_ROVI_CONFIGURATION, null)
            return Gson().fromJson(jsonConfiguration, RoviConfiguration::class.java)
        }
    override var introduction: String?
        get() {
            return prefs.getString(KEY_INTRODUCTION, null)
        }
        set(value) {
            prefs.edit { putString(KEY_INTRODUCTION, value) }
        }
    override var settingsLinks: SettingsLinks?
        set(value) {
            prefs.edit { putString(KEY_SETTINGS_LINKS, Gson().toJson(value)) }
        }
        get() {
            val jsonLinks = prefs.getString(KEY_SETTINGS_LINKS, null)
            return Gson().fromJson(jsonLinks, SettingsLinks::class.java)
        }
    override var language: String
        set(value) {
            prefs.edit { putString(KEY_LANGUAGE, value) }
        }
        get() = prefs.getString(KEY_LANGUAGE, Credentials.BASE_LANGUAGE.lang).toString()
    override var qr: String?
        set(value) = prefs.edit { putString(KEY_QR, value) }
        get() = prefs.getString(KEY_QR, null)

    override fun clear() {
        prefs.edit().clear().apply()
    }
}