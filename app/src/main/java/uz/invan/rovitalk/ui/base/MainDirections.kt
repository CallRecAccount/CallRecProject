package uz.invan.rovitalk.ui.base

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import uz.invan.rovitalk.R

enum class MainDirections(@IdRes val destId: Int, @StringRes val label: Int) {
    HOME(R.id.home, R.string.screen_home),
    SECTION(R.id.podcasts, R.string.screen_section),
    PROFILE(R.id.profile, R.string.screen_profile)
}