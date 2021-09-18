package uz.invan.rovitalk.data.models.validation

object NetworkResults {
    const val SUCCESS = 200
    const val VALIDATION_ERROR = 400
    const val UNAUTHORIZED = 401
    const val INCORRECT_OTP = 402
    const val FORBIDDEN = 403
    const val USER_NOT_FOUND = 404
    const val ROLE_NOT_FOUND = 404
    const val QR_NOT_FOUND = 404
    const val OTP_EXPIRED = 405
    const val BALANCE_NOT_ENOUGH = 405
    const val USER_ALREADY_EXISTS = 409
    const val QR_ALREADY_USED = 409
    const val TOO_MANY_ATTEMPTS = 429
    const val UNKNOWN_ERROR = 500
}