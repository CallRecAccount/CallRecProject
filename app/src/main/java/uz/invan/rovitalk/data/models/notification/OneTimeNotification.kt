package uz.invan.rovitalk.data.models.notification

import com.google.gson.annotations.SerializedName

data class OneTimeNotification(
    @SerializedName("titleObj")
    val titleObject: OneTimeObject,
    @SerializedName("contentObj")
    val contentObject: OneTimeObject,
)

data class OneTimeObject(
    val en: String,
    val ru: String,
    val uz: String,
)