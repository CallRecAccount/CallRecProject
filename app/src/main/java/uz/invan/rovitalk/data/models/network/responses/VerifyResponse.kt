package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class VerifyResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: VerifyResponseData,
)

data class VerifyResponseData(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phone: String,
    @SerializedName("image")
    val photo: String?,
    @SerializedName("token")
    val token: String,
)