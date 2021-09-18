package uz.invan.rovitalk.data.models.network.requests

import com.google.gson.annotations.SerializedName

data class VerifyRequest(
    @SerializedName("phone_number")
    val phone: String,
    @SerializedName("verification_code")
    val otp: String,
)