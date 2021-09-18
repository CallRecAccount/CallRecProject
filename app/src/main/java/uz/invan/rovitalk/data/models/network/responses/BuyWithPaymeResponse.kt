package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class BuyWithPaymeResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: BuyWithPaymeResponseData,
)

data class BuyWithPaymeResponseData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("payme_link")
    val paymeLink: String,
)