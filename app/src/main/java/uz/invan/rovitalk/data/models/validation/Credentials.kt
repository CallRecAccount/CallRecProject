package uz.invan.rovitalk.data.models.validation

import uz.invan.rovitalk.data.models.user.RoviLanguage

object Credentials {
    const val PHONE_NUMBER_LENGTH = 9
    const val PHONE_FORMAT_MASK = "+998 ([00]) [000]-[00]-[00]"
    const val OTP_LENGTH = 4
    const val FIRST_NAME_LENGTH = 3
    const val LAST_NAME_LENGTH = 3
    const val RESEND_ATTEMPTS_INTERVAL_IN_MINUTES = 2
    const val MIN_SECTIONS_UPDATE_INTERVAL_IN_MINUTES = 5
    const val QR_QUERY_DATA = "data"

    const val MAX_BUFFER_POSITION = 100
    const val MAX_VOLUME = 100
    const val PLAYER_SPEED = 1f

    const val TIME_DIVIDER = '-'
    const val TIME_LENGTH = 2

    val BASE_LANGUAGE = RoviLanguage.UZ
}