@file:Suppress("DEPRECATION")

package uz.invan.rovitalk.data.models.validation.exceptions

import uz.invan.rovitalk.data.models.validation.RoviExceptionMessages

// sections-exceptions
enum class SectionsExceptions {
    SECTION_BY_ID_NOT_FOUND_EXCEPTION
}

data class SectionsExceptionData(
    val exception: SectionsExceptions
)

abstract class SectionsException(override val message: String) : Exception(message) {
    abstract val data: SectionsExceptionData
}

class SectionByIdNotFoundException(
    override val data: SectionsExceptionData
) : SectionsException(RoviExceptionMessages.SECTION_BY_ID_NOT_FOUND_EXCEPTION)

// categories-exceptions
enum class CategoriesExceptions

abstract class CategoriesException(override val message: String) : Exception(message)
