@file:Suppress("DEPRECATION")

package uz.invan.rovitalk.data.models.validation.exceptions

import com.chuckerteam.chucker.api.ChuckerCollector
import uz.invan.rovitalk.data.models.validation.RoviExceptionMessages

enum class NetworkExceptions {
    IO_EXCEPTION, SOCKET_TIMEOUT_EXCEPTION
}

enum class NetworkAuthExceptions {
    VALIDATION_EXCEPTION, USER_NOT_FOUND_EXCEPTION, USER_ALREADY_EXISTS, TOO_MANY_ATTEMPTS_EXCEPTION, INCORRECT_OTP_EXCEPTION, OTP_EXPIRED_EXCEPTION, UNAUTHORIZED, UNKNOWN_EXCEPTION
}

// network-exceptions
data class NetworkExceptionData(
    val exception: NetworkExceptions,
    val throwable: Throwable,
)

abstract class NetworkException(override val message: String) : Exception(message) {
    abstract val data: NetworkExceptionData
}

class NetworkIOException(
    override val data: NetworkExceptionData,
    collector: ChuckerCollector,
) : NetworkException(RoviExceptionMessages.NETWORK_IO_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkSocketTimeOutException(
    override val data: NetworkExceptionData,
    collector: ChuckerCollector,
) : NetworkException(RoviExceptionMessages.NETWORK_SOCKET_TIME_OUT_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

// network-auth-exceptions
data class NetworkAuthExceptionData(
    val code: Int,
    val exception: NetworkAuthExceptions,
    val throwable: Throwable,
)

abstract class NetworkAuthException(override val message: String) : Exception(message) {
    abstract val data: NetworkAuthExceptionData
}

class NetworkAuthValidationException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_VALIDATION_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthUserNotFoundException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_USER_NOT_FOUND_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthUserAlreadyExistsException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_USER_ALREADY_EXISTS_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthTooManyAttemptsException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_TOO_MANY_ATTEMPTS_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthIncorrectOtpException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_INCORRECT_OTP_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthOtpExpiredException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_OTP_EXPIRED_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthUnauthorizedException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_UNAUTHORIZED_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

class NetworkAuthUnknownException(
    override val data: NetworkAuthExceptionData,
    collector: ChuckerCollector,
) : NetworkAuthException(RoviExceptionMessages.NETWORK_AUTH_UNKNOWN_EXCEPTION) {
    init {
        collector.onError(message, data.throwable)
    }
}

// network-podcasts-exception
object NetworkPodcasts {
    enum class NetworkPodcastsExceptions {
        VALIDATION_EXCEPTION, UNAUTHORIZED, FORBIDDEN, ROLE_NOT_FOUND_EXCEPTION, BALANCE_NOT_ENOUGH, UNKNOWN_EXCEPTION
    }

    data class NetworkPodcastsExceptionData(
        val code: Int,
        val exception: NetworkPodcastsExceptions,
        val throwable: Throwable,
    )

    abstract class NetworkPodcastsException(override val message: String) : Exception(message) {
        abstract val data: NetworkPodcastsExceptionData
    }

    class NetworkPodcastsValidationException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_VALIDATION_ERROR) {
        init {
            collector.onError(message, data.throwable)
        }
    }

    class NetworkPodcastsUnauthorizedException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_UNAUTHORIZED_EXCEPTION) {
        init {
            collector.onError(message, data.throwable)
        }
    }

    class NetworkPodcastsForbiddenException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_FORBIDDEN) {
        init {
            collector.onError(message, data.throwable)
        }
    }

    class NetworkPodcastsRoleNotFoundException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_ROLE_NOT_FOUND_EXCEPTION) {
        init {
            collector.onError(message, data.throwable)
        }
    }

    class NetworkPodcastsBalanceNotEnoughException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_BALANCE_NOT_ENOUGH) {
        init {
            collector.onError(message, data.throwable)
        }
    }

    class NetworkPodcastsUnknownException(
        override val data: NetworkPodcastsExceptionData,
        collector: ChuckerCollector,
    ) : NetworkPodcastsException(RoviExceptionMessages.NETWORK_PODCASTS_UNKNOWN_EXCEPTION) {
        init {
            collector.onError(message, data.throwable)
        }
    }
}