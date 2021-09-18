package uz.invan.rovitalk.data.models.network.requests

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("phone_number")
    val phone: String,
)