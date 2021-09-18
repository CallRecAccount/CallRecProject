package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class BuyWithClickResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: BuyWithClickResponseData,
)

data class BuyWithClickResponseData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("click_link")
    val clickLink: String,
)