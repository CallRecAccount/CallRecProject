package uz.invan.rovitalk.data.remote

import com.chuckerteam.chucker.api.ChuckerCollector
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import uz.invan.rovitalk.data.models.network.requests.*
import uz.invan.rovitalk.data.models.network.responses.*
import uz.invan.rovitalk.data.models.validation.NetworkResults
import uz.invan.rovitalk.data.models.validation.exceptions.*
import uz.invan.rovitalk.util.ktx.toRoviError
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RoviApiHelper @Inject constructor(
    private val api: RoviApi,
    private val collector: ChuckerCollector,
) : RoviApi {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse {
        try {
            return api.login(loginRequest)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkAuthValidationException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkAuthExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.USER_NOT_FOUND -> {
                            throw NetworkAuthUserNotFoundException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.USER_NOT_FOUND,
                                    exception = NetworkAuthExceptions.USER_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.TOO_MANY_ATTEMPTS -> {
                            throw NetworkAuthTooManyAttemptsException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.TOO_MANY_ATTEMPTS,
                                    exception = NetworkAuthExceptions.TOO_MANY_ATTEMPTS_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        else -> {
                            throw NetworkAuthUnknownException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkAuthUnknownException(
                        data = NetworkAuthExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): RegisterRequest {
        try {
            return api.register(registerRequest)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkAuthValidationException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkAuthExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.USER_ALREADY_EXISTS -> {
                            throw NetworkAuthUserAlreadyExistsException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.USER_ALREADY_EXISTS,
                                    exception = NetworkAuthExceptions.USER_ALREADY_EXISTS,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        else -> {
                            throw NetworkAuthUnknownException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkAuthUnknownException(
                        data = NetworkAuthExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun verify(verifyRequest: VerifyRequest): VerifyResponse {
        try {
            return api.verify(verifyRequest)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.INCORRECT_OTP -> {
                            throw NetworkAuthIncorrectOtpException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.INCORRECT_OTP,
                                    exception = NetworkAuthExceptions.INCORRECT_OTP_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.USER_NOT_FOUND -> {
                            throw NetworkAuthUserNotFoundException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.USER_NOT_FOUND,
                                    exception = NetworkAuthExceptions.USER_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.OTP_EXPIRED -> {
                            throw NetworkAuthOtpExpiredException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.OTP_EXPIRED,
                                    exception = NetworkAuthExceptions.OTP_EXPIRED_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        else -> {
                            throw NetworkAuthUnknownException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkAuthUnknownException(
                        data = NetworkAuthExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun editProfile(editProfileRequest: EditProfileRequest): EditProfileResponse {
        try {
            return api.editProfile(editProfileRequest)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkAuthValidationException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkAuthExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkAuthUnauthorizedException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkAuthExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.USER_NOT_FOUND -> {
                            throw NetworkAuthUserNotFoundException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.USER_NOT_FOUND,
                                    exception = NetworkAuthExceptions.USER_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.USER_ALREADY_EXISTS -> {
                            throw NetworkAuthUserAlreadyExistsException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.USER_ALREADY_EXISTS,
                                    exception = NetworkAuthExceptions.USER_ALREADY_EXISTS,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        else -> {
                            throw NetworkAuthUnknownException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkAuthUnknownException(
                        data = NetworkAuthExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun uploadImage(image: MultipartBody.Part): UploadFileResponse {
        try {
            return api.uploadImage(image)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkAuthValidationException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkAuthExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkAuthUnauthorizedException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkAuthExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        else -> {
                            throw NetworkAuthUnknownException(
                                data = NetworkAuthExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkAuthUnknownException(
                        data = NetworkAuthExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkAuthExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveSections(): SectionsResponse {
        try {
            return api.retrieveSections()
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveActiveSections(): ActiveSectionsResponse {
        try {
            return api.retrieveActiveSections()
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveCategories(sectionId: String): CategoriesResponse {
        try {
            return api.retrieveCategories(sectionId)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveSubCategories(subCategory: SubCategoriesRequest): SubCategoriesResponse {
        try {
            return api.retrieveSubCategories(subCategory)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveIntroductions(): IntroductionsResponse {
        try {
            return api.retrieveIntroductions()
        } catch (exception: Exception) {
            when (exception) {
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveMostListenerCategories(): CategoriesResponse {
        try {
            return api.retrieveMostListenerCategories()
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrievePrices(): PricesResponse {
        try {
            return api.retrievePrices()
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun buy(buy: BuyRequest) {
        try {
            return api.buy(buy)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.BALANCE_NOT_ENOUGH -> {
                            throw NetworkPodcasts.NetworkPodcastsBalanceNotEnoughException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.BALANCE_NOT_ENOUGH,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.BALANCE_NOT_ENOUGH,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun fetchTotalAmount(buy: BuyRequest): BuyTotalResponse {
        try {
            return api.fetchTotalAmount(buy)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }

        }
    }

    override suspend fun buyWithPayme(buy: BuyRequest): BuyWithPaymeResponse {
        try {
            return api.buyWithPayme(buy)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }

        }
    }

    override suspend fun buyWithClick(buy: BuyRequest): BuyWithClickResponse {
        try {
            return api.buyWithClick(buy)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }

        }
    }

    override suspend fun buyWithRoboCassa(buy: BuyRequest): BuyWithRoboKassaResponse {
        try {
            return api.buyWithRoboCassa(buy)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }

        }
    }

    override suspend fun retrieveBackgroundMusics(categoryId: String): BackgroundMusicResponse {
        try {
            return api.retrieveBackgroundMusics(categoryId)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveCategoryFiles(categoryId: String): CategoryFilesResponse {
        try {
            return api.retrieveCategoryFiles(categoryId)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveFile(fileId: String): FileResponse {
        try {
            return api.retrieveFile(fileId)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.FORBIDDEN -> {
                            throw NetworkPodcasts.NetworkPodcastsForbiddenException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.FORBIDDEN,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.FORBIDDEN,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun streamFile(fileUrl: String): ResponseBody {
        try {
            return api.streamFile(fileUrl)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun qr(qr: QRRequest) {
        try {
            return api.qr(qr)
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.VALIDATION_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsValidationException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.VALIDATION_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.VALIDATION_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.QR_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.QR_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.QR_ALREADY_USED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.QR_ALREADY_USED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }

    override suspend fun retrieveNotifications(): NotificationsResponse {
        try {
            return api.retrieveNotifications()
        } catch (exception: Exception) {
            when (exception) {
                is HttpException -> {
                    when (exception.toRoviError()?.code) {
                        NetworkResults.UNAUTHORIZED -> {
                            throw NetworkPodcasts.NetworkPodcastsUnauthorizedException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNAUTHORIZED,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNAUTHORIZED,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.ROLE_NOT_FOUND -> {
                            throw NetworkPodcasts.NetworkPodcastsRoleNotFoundException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.ROLE_NOT_FOUND,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.ROLE_NOT_FOUND_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                        NetworkResults.UNKNOWN_ERROR -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector
                            )
                        }
                        else -> {
                            throw NetworkPodcasts.NetworkPodcastsUnknownException(
                                data = NetworkPodcasts.NetworkPodcastsExceptionData(
                                    code = NetworkResults.UNKNOWN_ERROR,
                                    exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                                    throwable = exception
                                ), collector = collector
                            )
                        }
                    }
                }
                is SocketTimeoutException -> {
                    throw NetworkSocketTimeOutException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.SOCKET_TIMEOUT_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                is IOException -> {
                    throw NetworkIOException(
                        data = NetworkExceptionData(
                            exception = NetworkExceptions.IO_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
                else -> {
                    throw NetworkPodcasts.NetworkPodcastsUnknownException(
                        data = NetworkPodcasts.NetworkPodcastsExceptionData(
                            code = NetworkResults.UNKNOWN_ERROR,
                            exception = NetworkPodcasts.NetworkPodcastsExceptions.UNKNOWN_EXCEPTION,
                            throwable = exception
                        ), collector = collector
                    )
                }
            }
        }
    }
}