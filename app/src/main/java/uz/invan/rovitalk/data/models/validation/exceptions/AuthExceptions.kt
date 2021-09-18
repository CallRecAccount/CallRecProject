package uz.invan.rovitalk.data.models.validation.exceptions

import uz.invan.rovitalk.data.models.validation.RoviExceptionMessages

enum class LoginExceptions {
    PHONE_LENGTH_EXCEPTION
}

enum class VerifyExceptions {
    VERIFY_PHONE_LENGTH_EXCEPTION, VERIFY_OTP_LENGTH_EXCEPTION
}

enum class RegisterExceptions {
    PHONE_LENGTH_EXCEPTION,
    FIRST_NAME_LENGTH_EXCEPTION,
    LAST_NAME_LENGTH_EXCEPTION,
    AGREEMENT_EXCEPTION
}

enum class ResendExceptions {
    PHONE_LENGTH_EXCEPTION
}

enum class EditProfileExceptions {
    FIRST_NAME_LENGTH_EXCEPTION,
    LAST_NAME_LENGTH_EXCEPTION
}

// credential data's
data class LengthCredentialData(
    val data: String,
    val length: Int,
)

// login-exceptions
data class LoginExceptionData(
    val phone: LengthCredentialData,
    val exception: LoginExceptions,
)

abstract class LoginException(override val message: String) : Exception(message) {
    abstract val data: LoginExceptionData
}

class LoginPhoneLengthException(
    override val data: LoginExceptionData,
) : LoginException(RoviExceptionMessages.LOGIN_PHONE_LENGTH_EXCEPTION)

// register-exceptions
data class RegisterExceptionData(
    val phone: LengthCredentialData,
    val firstName: LengthCredentialData,
    val lastName: LengthCredentialData,
    val exception: RegisterExceptions,
)

abstract class RegisterException(override val message: String) : Exception(message) {
    abstract val data: RegisterExceptionData
}

class RegisterPhoneLengthException(
    override val data: RegisterExceptionData,
) : RegisterException(RoviExceptionMessages.REGISTER_PHONE_LENGTH_EXCEPTION)

class RegisterFirstNameLengthException(
    override val data: RegisterExceptionData,
) : RegisterException(RoviExceptionMessages.REGISTER_FIRST_NAME_LENGTH_EXCEPTION)

class RegisterLastNameException(
    override val data: RegisterExceptionData,
) : RegisterException(RoviExceptionMessages.REGISTER_LAST_NAME_LENGTH_EXCEPTION)

class RegisterAgreementException(
    override val data: RegisterExceptionData,
) : RegisterException(RoviExceptionMessages.REGISTER_AGREEMENT_EXCEPTION)

// verify-exceptions
data class VerifyExceptionData(
    val phone: LengthCredentialData,
    val otp: LengthCredentialData,
    val exception: VerifyExceptions,
)

abstract class VerifyException(override val message: String) : Exception(message) {
    abstract val data: VerifyExceptionData
}

class VerifyPhoneLengthException(
    override val data: VerifyExceptionData,
) : VerifyException(RoviExceptionMessages.VERIFY_PHONE_LENGTH_EXCEPTION)

class VerifyOtpLengthException(
    override val data: VerifyExceptionData,
) : VerifyException(RoviExceptionMessages.VERIFY_OTP_LENGTH_EXCEPTION)

// resend-exceptions
data class ResendExceptionData(
    val phone: LengthCredentialData,
    val exception: ResendExceptions,
)

abstract class ResendException(override val message: String) : Exception(message) {
    abstract val data: ResendExceptionData
}

class ResendPhoneLengthException(
    override val data: ResendExceptionData,
) : ResendException(RoviExceptionMessages.RESEND_PHONE_LENGTH_EXCEPTION)

// edit-profile-exceptions
data class EditProfileExceptionData(
    val firstName: LengthCredentialData,
    val lastName: LengthCredentialData,
    val exception: EditProfileExceptions,
)

abstract class EditProfileException(
    override val message: String,
) : Exception(message) {
    abstract val data: EditProfileExceptionData
}

class EditProfileFirstNameLengthException(
    override val data: EditProfileExceptionData,
) : EditProfileException(RoviExceptionMessages.EDIT_PROFILE_FIRST_NAME_LENGTH_EXCEPTION)

class EditProfileLastNameLengthException(
    override val data: EditProfileExceptionData,
) : EditProfileException(RoviExceptionMessages.EDIT_PROFILE_LAST_NAME_LENGTH_EXCEPTION)