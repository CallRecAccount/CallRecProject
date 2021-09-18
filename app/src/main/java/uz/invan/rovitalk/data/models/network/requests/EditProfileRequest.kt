package uz.invan.rovitalk.data.models.network.requests

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("image")
    val photo: String?,
)