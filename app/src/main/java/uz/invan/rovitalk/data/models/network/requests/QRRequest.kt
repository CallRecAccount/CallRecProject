package uz.invan.rovitalk.data.models.network.requests


import com.google.gson.annotations.SerializedName

data class QRRequest(
    @SerializedName("qr_code")
    val qrCode: String,
)