package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class RoviErrorResponseData(
    @SerializedName("message")
    val message: String,
    @SerializedName("statusCode")
    val code: Int,
)