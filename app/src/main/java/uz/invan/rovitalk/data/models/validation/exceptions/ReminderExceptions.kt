package uz.invan.rovitalk.data.models.validation.exceptions

import uz.invan.rovitalk.data.models.validation.RoviExceptionMessages

// reminder-exception-types
enum class ReminderExceptions {
    TITLE_EMPTY_EXCEPTION, CONTENT_EMPTY_EXCEPTION, TIME_WRONG_EXCEPTION
}

// reminder-exception-data-holder
data class ReminderExceptionData(
    val exception: ReminderExceptions,
)

// abstract-reminder-exception
abstract class ReminderException(override val message: String) : Exception(message) {
    abstract val data: ReminderExceptionData
}

// reminder exceptions
class ReminderTitleEmptyException(
    override val data: ReminderExceptionData,
) : ReminderException(RoviExceptionMessages.REMINDER_TITLE_EMPTY_EXCEPTION)

class ReminderContentEmptyException(
    override val data: ReminderExceptionData,
) : ReminderException(RoviExceptionMessages.REMINDER_CONTENT_EMPTY_EXCEPTION)

class ReminderTimeWrongException(
    override val data: ReminderExceptionData,
) : ReminderException(RoviExceptionMessages.REMINDER_TIME_WRONG_EXCEPTION)