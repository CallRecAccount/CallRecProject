package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<NotificationData>,
)

data class NotificationData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("time")
    val time: String,
    @SerializedName("type")
    val type: String,
)