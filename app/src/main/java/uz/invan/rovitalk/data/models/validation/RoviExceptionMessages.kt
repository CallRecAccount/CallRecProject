package uz.invan.rovitalk.data.models.validation

import uz.invan.rovitalk.data.models.validation.Credentials.FIRST_NAME_LENGTH
import uz.invan.rovitalk.data.models.validation.Credentials.LAST_NAME_LENGTH
import uz.invan.rovitalk.data.models.validation.Credentials.OTP_LENGTH
import uz.invan.rovitalk.data.models.validation.Credentials.PHONE_NUMBER_LENGTH
import uz.invan.rovitalk.data.models.validation.Credentials.RESEND_ATTEMPTS_INTERVAL_IN_MINUTES
import uz.invan.rovitalk.data.models.validation.NetworkResults.BALANCE_NOT_ENOUGH
import uz.invan.rovitalk.data.models.validation.NetworkResults.FORBIDDEN
import uz.invan.rovitalk.data.models.validation.NetworkResults.INCORRECT_OTP
import uz.invan.rovitalk.data.models.validation.NetworkResults.OTP_EXPIRED
import uz.invan.rovitalk.data.models.validation.NetworkResults.ROLE_NOT_FOUND
import uz.invan.rovitalk.data.models.validation.NetworkResults.TOO_MANY_ATTEMPTS
import uz.invan.rovitalk.data.models.validation.NetworkResults.UNAUTHORIZED
import uz.invan.rovitalk.data.models.validation.NetworkResults.UNKNOWN_ERROR
import uz.invan.rovitalk.data.models.validation.NetworkResults.USER_ALREADY_EXISTS
import uz.invan.rovitalk.data.models.validation.NetworkResults.USER_NOT_FOUND
import uz.invan.rovitalk.data.models.validation.NetworkResults.VALIDATION_ERROR

object RoviExceptionMessages {
    // auth
    const val LOGIN_PHONE_LENGTH_EXCEPTION =
        "Length of phone number wrong. Phone number length must be equal: $PHONE_NUMBER_LENGTH"
    const val REGISTER_PHONE_LENGTH_EXCEPTION =
        "Length of phone number wrong. Phone number length must be equal: $PHONE_NUMBER_LENGTH"
    const val REGISTER_FIRST_NAME_LENGTH_EXCEPTION =
        "Length of first name wrong. First name length must be equal or greater than: $FIRST_NAME_LENGTH"
    const val REGISTER_LAST_NAME_LENGTH_EXCEPTION =
        "Length of last name wrong. Last name length must be equal or greater than: $LAST_NAME_LENGTH"
    const val REGISTER_AGREEMENT_EXCEPTION =
        "User have to accept terms and conditions in order to create account"
    const val VERIFY_PHONE_LENGTH_EXCEPTION =
        "Length of phone number wrong. Phone number length must be equal: $PHONE_NUMBER_LENGTH"
    const val VERIFY_OTP_LENGTH_EXCEPTION =
        "Length of verification otp wrong. Verification otp length must be equal: $OTP_LENGTH"
    const val RESEND_PHONE_LENGTH_EXCEPTION =
        "Length of phone number wrong in resend. Phone number length must be equal: $PHONE_NUMBER_LENGTH"
    const val EDIT_PROFILE_PHONE_LENGTH_EXCEPTION =
        "Length of phone number wrong in edit profile. Phone number length must be equal: $PHONE_NUMBER_LENGTH"
    const val EDIT_PROFILE_FIRST_NAME_LENGTH_EXCEPTION =
        "Length of first name wrong in edit profile. First name length must be equal or greater than: $FIRST_NAME_LENGTH"
    const val EDIT_PROFILE_LAST_NAME_LENGTH_EXCEPTION =
        "Length of last name wrong in edit profile. Last name length must be equal or greater than: $LAST_NAME_LENGTH"

    // network
    const val NETWORK_IO_EXCEPTION =
        "Network io exception threw because no connection with server/internet"
    const val NETWORK_SOCKET_TIME_OUT_EXCEPTION =
        "Network socket time out exception threw because request take a long time and canceled"

    // network-auth
    const val NETWORK_AUTH_VALIDATION_EXCEPTION =
        "Network auth validation exception returned from server with $VALIDATION_ERROR code"
    const val NETWORK_AUTH_USER_NOT_FOUND_EXCEPTION =
        "Network auth user not found exception returned from server, that user not authorized with give phone number and with $USER_NOT_FOUND code"
    const val NETWORK_AUTH_USER_ALREADY_EXISTS_EXCEPTION =
        "Network auth user already exists returned from server, that user not already authorized with give phone number and with $USER_ALREADY_EXISTS code"
    const val NETWORK_AUTH_TOO_MANY_ATTEMPTS_EXCEPTION =
        "Network auth too many attempts exception returned from server, that user authenticated more times in $RESEND_ATTEMPTS_INTERVAL_IN_MINUTES interval and with $TOO_MANY_ATTEMPTS code"
    const val NETWORK_AUTH_INCORRECT_OTP_EXCEPTION =
        "Network auth incorrect otp exception returned from service, because otp does not match with sent otp and $INCORRECT_OTP code"
    const val NETWORK_AUTH_OTP_EXPIRED_EXCEPTION =
        "Network auth otp expired exception, that otp sent to user already expired and $OTP_EXPIRED code"
    const val NETWORK_AUTH_UNAUTHORIZED_EXCEPTION =
        "Network auth unauthorized exception, that user token is invalid or expired and $UNAUTHORIZED code"
    const val NETWORK_AUTH_UNKNOWN_EXCEPTION =
        "Network auth unknown exception happened, no one knows what happened maybe server broken, code $UNKNOWN_ERROR"

    // network-podcast
    const val NETWORK_PODCASTS_VALIDATION_ERROR =
        "Network podcasts validation error returned from server with $VALIDATION_ERROR code"
    const val NETWORK_PODCASTS_UNAUTHORIZED_EXCEPTION =
        "Network podcasts unauthorized exception, that user token is invalid or expired and $UNAUTHORIZED code"
    const val NETWORK_PODCASTS_FORBIDDEN =
        "Network podcasts forbidden exception, that user is restricted to do this operation and $FORBIDDEN code"
    const val NETWORK_PODCASTS_ROLE_NOT_FOUND_EXCEPTION =
        "Network podcasts role not found exception returned from server, that role enter in header is invalid and $ROLE_NOT_FOUND code"
    const val NETWORK_PODCASTS_BALANCE_NOT_ENOUGH =
        "Network podcasts balance not enough exception returned from server, that balance of user is not enough to bu and $BALANCE_NOT_ENOUGH code"
    const val NETWORK_PODCASTS_UNKNOWN_EXCEPTION =
        "Network podcasts unknown exception happened, no one knows what happened maybe server broke, code $UNKNOWN_ERROR"

    // podcasts
    const val SECTION_BY_ID_NOT_FOUND_EXCEPTION =
        "Section not found from database by given id. Check id validity and try again"

    // reminder
    const val REMINDER_TITLE_EMPTY_EXCEPTION =
        "Reminder title empty exception, because title must not be empty"
    const val REMINDER_CONTENT_EMPTY_EXCEPTION =
        "Reminder content empty exception, because reminder content must not be empty"
    const val REMINDER_TIME_WRONG_EXCEPTION =
        "Reminder time wrong exception, because time is incorrect or empty"
}