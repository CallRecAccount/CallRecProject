package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("statusCode")
    val code: Int,
)