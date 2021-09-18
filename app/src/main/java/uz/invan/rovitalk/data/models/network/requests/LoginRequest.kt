package uz.invan.rovitalk.data.models.network.requests

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("phone_number")
    val phone: String,
)