package uz.invan.rovitalk.data.models.notification

import com.google.gson.annotations.SerializedName

data class FileCreateNotification(
    val title: String,
    val author: String,
    val duration: Long,
    @SerializedName("_id")
    val id: String,
)