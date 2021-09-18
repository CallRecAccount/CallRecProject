package uz.invan.rovitalk.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.*

class RoviContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(
            context: Context,
            language: String?,
        ): Context {
            var mContext = context
            val res: Resources = context.resources
            val configuration: Configuration = res.configuration
            val newLocale = Locale(language!!)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(newLocale)
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                mContext = context.createConfigurationContext(configuration)
            }

            return mContext
        }
    }
}