package uz.invan.rovitalk.data.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.invan.rovitalk.data.local.database.RoviDatabase
import uz.invan.rovitalk.data.local.prefs.RoviPrefs
import uz.invan.rovitalk.data.models.Resource
import uz.invan.rovitalk.data.models.network.requests.EditProfileRequest
import uz.invan.rovitalk.data.models.network.requests.LoginRequest
import uz.invan.rovitalk.data.models.network.requests.RegisterRequest
import uz.invan.rovitalk.data.models.network.requests.VerifyRequest
import uz.invan.rovitalk.data.models.settings.SettingsLinks
import uz.invan.rovitalk.data.models.user.Profile
import uz.invan.rovitalk.data.models.user.RoviLanguage
import uz.invan.rovitalk.data.models.validation.Credentials
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.data.remote.RoviApiHelper
import uz.invan.rovitalk.util.ktx.toFormData
import java.io.File
import javax.inject.Inject

interface AuthRepository {
    /**
     * Resend sms interval in minutes
     * */
    val resendIntervalInMinutes: Int

    /**
     * Phone format mask
     * */
    val phoneFormatMask: String

    /**
     * Default rovi language
     * */
    val defaultLanguage: RoviLanguage
        get() = RoviLanguage.UZ

    /**
     * Login to user's account by given phone number, if account not exists returns [Exception]
     * Must call in [CoroutineScope]
     * @param phone - phone number of user which wants to login with
     * @param isValidFormattedNumber phone is valid with country code or not
     * @throws LoginPhoneLengthException - if phone digits length are incorrect
     * */
    @Throws(exceptionClasses = [LoginPhoneLengthException::class])
    suspend fun login(phone: String, isValidFormattedNumber: Boolean): Flow<Resource<Unit>>

    /**
     * Register's new user account by given phone number, first-name, last-name, if account exists returns [Exception]
     * Must call in [CoroutineScope]
     * @param phone - phone number of user, which account will be created with
     * @param firstName - First name of new user
     * @param lastName - Last name of new user
     * @param isAgreed - Whether user agreed or not to terms and conditions
     * @throws RegisterPhoneLengthException - if phone digits length are incorrect
     * @throws RegisterFirstNameLengthException - if first name of user is short
     * @throws RegisterLastNameException - if last name of user is short
     * @throws RegisterAgreementException - if user not agreed with terms and conditions
     * */
    @Throws(exceptionClasses = [RegisterPhoneLengthException::class, RegisterFirstNameLengthException::class, RegisterLastNameException::class, RegisterAgreementException::class])
    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        isAgreed: Boolean,
    ): Flow<Resource<Unit>>

    /**
     * Verify's user's account to log in by phone and otp
     * Must call in [CoroutineScope]
     * @param phone - entered in login/register
     * @param otp - 4 digit number sent to users phone
     * @throws VerifyOtpLengthException - if otp digits length are incorrect
     * */
    @Throws(exceptionClasses = [VerifyOtpLengthException::class])
    suspend fun verify(phone: String, otp: String): Flow<Resource<Unit>>

    /**
     * Resend's otp by given [phone] again
     * For now it's equal with [login] method, only exceptions differs
     * */
    @Throws(exceptionClasses = [LoginPhoneLengthException::class])
    suspend fun resendOtp(phone: String): Flow<Resource<Unit>>

    /**
     * Edit's users profile by given [firstName], [lastName]
     * Must call in [CoroutineScope]
     * @param firstName - First name of user which will be changed
     * @param lastName - Last name of user which will be changed
     * @param photo - Photo of user
     * @throws EditProfileFirstNameLengthException - when first name is incorrect
     * @throws EditProfileLastNameLengthException - when last name is incorrect
     * */
    @Throws(exceptionClasses = [EditProfileFirstNameLengthException::class, EditProfileLastNameLengthException::class])
    suspend fun editProfile(
        firstName: String,
        lastName: String,
        photo: File?,
    ): Flow<Resource<Profile?>>

    /**
     * Uploads users image to server
     * Must call in [CoroutineScope]
     * @param file image which will be uploaded
     * @return [String] path to file
     * */
    suspend fun uploadImage(file: File): String

    /**
     * Returns users profile saved in preferences
     * Must call in [CoroutineScope]
     * */
    fun retrieveProfile(): Flow<Resource<Profile?>>

    /**
     * Returns user is logged in or not
     * Must call in [CoroutineScope]
     * @return `boolean` true if user logged in
     * */
    suspend fun isUserAuthenticated(): Boolean

    /**
     * Log outs user from application, all user download data will be deleted
     * Must call in [CoroutineScope]
     * */
    suspend fun logout()

    /**
     * Retrieves introduction video from preferences
     * @return [String] link of video if exits
     * */
    fun fetchIntroduction(): String?

    /**
     * Retrieves settings links from [RoviPrefs]
     * @return [SettingsLinks] list of settings links
     * */
    fun fetchSettingsLinks(): SettingsLinks?


    /**
     * Changes applications language
     * @param lang language which application will be changed
     * @return [Boolean] true if language updated
     * */
    suspend fun changeLanguage(lang: String): Boolean

    /**
     * Returns current language of rovi
     * @return [RoviLanguage] current rovi language
     * */
    fun fetchLanguage(): RoviLanguage
}

class AuthRepositoryImpl @Inject constructor(
    private val apiHelper: RoviApiHelper,
    private val database: RoviDatabase,
    private val prefs: RoviPrefs,
    override val resendIntervalInMinutes: Int, override val phoneFormatMask: String,
) : AuthRepository {
    override suspend fun login(phone: String, isValidFormattedNumber: Boolean) = flow {
        if (!isValidFormattedNumber)
            throw LoginPhoneLengthException(
                LoginExceptionData(
                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
                    exception = LoginExceptions.PHONE_LENGTH_EXCEPTION
                )
            )
        // emits loading state
        emit(Resource.Loading())
        // creates LoginRequest and sends request
        apiHelper.login(LoginRequest(phone))
        // emits success
        emit(Resource.Success(Unit))
    }

    override suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        isAgreed: Boolean,
    ) = flow {
//        if (phone.length != Credentials.PHONE_NUMBER_LENGTH)
//            throw RegisterPhoneLengthException(
//                RegisterExceptionData(
//                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
//                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
//                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
//                    exception = RegisterExceptions.PHONE_LENGTH_EXCEPTION
//                )
//            )
        if (firstName.length < Credentials.FIRST_NAME_LENGTH)
            throw RegisterPhoneLengthException(
                RegisterExceptionData(
                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
                    exception = RegisterExceptions.FIRST_NAME_LENGTH_EXCEPTION
                )
            )
        if (lastName.length < Credentials.LAST_NAME_LENGTH)
            throw RegisterPhoneLengthException(
                RegisterExceptionData(
                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
                    exception = RegisterExceptions.LAST_NAME_LENGTH_EXCEPTION
                )
            )
        if (!isAgreed)
            throw RegisterAgreementException(
                RegisterExceptionData(
                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
                    exception = RegisterExceptions.AGREEMENT_EXCEPTION
                )
            )

        // emits loading state
        emit(Resource.Loading())
        // creates RegisterRequest and sends request
        apiHelper.register(RegisterRequest(firstName, lastName, phone))
        // emits success
        emit(Resource.Success(Unit))
    }

    override suspend fun verify(phone: String, otp: String) = flow {
//        if (phone.length != Credentials.PHONE_NUMBER_LENGTH)
//            throw VerifyPhoneLengthException(
//                VerifyExceptionData(
//                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
//                    otp = LengthCredentialData(otp, Credentials.OTP_LENGTH),
//                    exception = VerifyExceptions.VERIFY_PHONE_LENGTH_EXCEPTION
//                )
//            )
        if (otp.length != Credentials.OTP_LENGTH)
            throw VerifyOtpLengthException(
                VerifyExceptionData(
                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
                    otp = LengthCredentialData(otp, Credentials.OTP_LENGTH),
                    exception = VerifyExceptions.VERIFY_OTP_LENGTH_EXCEPTION
                )
            )

        // emits loading state
        emit(Resource.Loading())
        // creates VerifyRequest and sends request
        val verify = apiHelper.verify(VerifyRequest(phone, otp))
        // save profile to preferences
        val profile = Profile(
            phone = verify.data.phone,
            firstName = verify.data.firstName,
            lastName = verify.data.lastName,
            photo = verify.data.photo,
            token = verify.data.token
        )
        prefs.profile = profile
        // emits success
        emit(Resource.Success(Unit))
    }

    override suspend fun resendOtp(phone: String) = flow {
//        if (phone.length != Credentials.PHONE_NUMBER_LENGTH)
//            throw ResendPhoneLengthException(
//                ResendExceptionData(
//                    phone = LengthCredentialData(phone, Credentials.PHONE_NUMBER_LENGTH),
//                    exception = ResendExceptions.PHONE_LENGTH_EXCEPTION
//                )
//            )

        // emits loading state
        emit(Resource.Loading())
        // creates LoginRequest and sends request
        apiHelper.login(LoginRequest(phone))
        // emits success
        emit(Resource.Success(Unit))
    }

    override suspend fun editProfile(
        firstName: String,
        lastName: String,
        photo: File?,
    ): Flow<Resource<Profile?>> = flow {
        if (firstName.length < Credentials.FIRST_NAME_LENGTH)
            throw EditProfileFirstNameLengthException(
                EditProfileExceptionData(
                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
                    exception = EditProfileExceptions.FIRST_NAME_LENGTH_EXCEPTION
                )
            )

        if (lastName.length < Credentials.LAST_NAME_LENGTH)
            throw EditProfileLastNameLengthException(
                EditProfileExceptionData(
                    firstName = LengthCredentialData(firstName, Credentials.FIRST_NAME_LENGTH),
                    lastName = LengthCredentialData(lastName, Credentials.LAST_NAME_LENGTH),
                    exception = EditProfileExceptions.LAST_NAME_LENGTH_EXCEPTION
                )
            )

        // emits loading state
        emit(Resource.Loading())
        // retrieves photo path if photo set
        val path = photo?.run {
            uploadImage(this)
        } ?: prefs.profile?.photo

        // creates Edit Profile Request and sends request
        apiHelper.editProfile(EditProfileRequest(firstName, lastName, path))
        val profile = prefs.profile?.copy(firstName = firstName, lastName = lastName, photo = path)
        prefs.profile = profile
        // emits success
        emit(Resource.Success(profile))
    }

    override suspend fun uploadImage(file: File): String {
        // converts `file` to form data and sends it
        val response = apiHelper.uploadImage(file.toFormData())
        return response.data.path
    }

    override fun retrieveProfile() = flow {
        val profile = prefs.profile
        emit(Resource.Success(profile))
    }

    override suspend fun isUserAuthenticated(): Boolean {
        val profile = prefs.profile
        return profile?.token != null
    }

    override suspend fun logout() {
        database.apply {
            sectionsAndCategoriesDao().apply {
                clearSubCategories()
                clearCategories()
                clearSections()
            }
            bmDao().clearBMs()
            audioDao().clearAudios()
            priceDao().clearPrices()
            reminderDao().clearReminders()
        }
        prefs.currentAudio = null
        prefs.profile = null
        prefs.configuration = null
        prefs.introduction = null
    }

    override fun fetchIntroduction(): String? {
        return prefs.introduction
    }

    override fun fetchSettingsLinks(): SettingsLinks? {
        return prefs.settingsLinks
    }

    override suspend fun changeLanguage(lang: String): Boolean {
        val language = fetchLanguage()
        if (lang == language.lang) return false

        // clearing database
        database.sectionsAndCategoriesDao().clearSections()
        database.sectionsAndCategoriesDao().clearCategories()
        database.sectionsAndCategoriesDao().clearSubCategories()
        database.audioDao().clearAudios()
        database.bmDao().clearBMs()
        database.priceDao().clearPrices()
        // updating language
        prefs.language = lang
        return true
    }

    override fun fetchLanguage(): RoviLanguage {
        val language = prefs.language

        val languages = RoviLanguage.values()
        return languages.firstOrNull { it.lang == language } ?: defaultLanguage
    }
}