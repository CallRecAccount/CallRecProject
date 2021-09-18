package uz.invan.rovitalk.data.models.validation

data class RoviConfiguration(
    val sectionsAndCategoriesLastUpdatedTime: Long? = null,
    var time: Long? = System.currentTimeMillis(),
    // settings
    val isRemindingEnabled: Boolean = false,
)