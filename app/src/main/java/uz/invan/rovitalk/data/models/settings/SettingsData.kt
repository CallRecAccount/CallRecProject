package uz.invan.rovitalk.data.models.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uz.invan.rovitalk.R

data class SettingsData(
    val image: String,
    val text: String,
    val callback: OnSettingsClickListener,
)

fun interface OnSettingsClickListener {
    fun onSettingsClick(setting: RoviSettings)
}

enum class RoviSettings(
    @DrawableRes val image: Int,
    @StringRes val text: Int,
) {
    FAQ(R.drawable.ic_baseline_help_24, R.string.faq_settings),
    DAILY_NOTIFICATIONS(
        R.drawable.ic_baseline_notifications_24,
        R.string.daily_notifications_settings
    ),
    RESTORE_PURCHASE(R.drawable.ic_baseline_restore_purchase_24, R.string.restore_purchase_setting),
    ABOUT_ROVI(R.drawable.ic_rovi_icon, R.string.about_rovi_setting),
    APP_RATING(R.drawable.ic_baseline_star_24, R.string.app_rating_setting),
    COMMENT(R.drawable.ic_baseline_add_comment_24, R.string.comment_your_thoughts_settings),
    LAW_INSTRUCTIONS(R.drawable.ic_baseline_verified_24, R.string.law_instructions_settings)
}

data class SettingsLinks(
    val links: List<SettingsLinkData>,
)

data class SettingsLinkData(
    val type: String,
    val url: String,
)