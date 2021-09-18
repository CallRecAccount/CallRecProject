package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class BuyWithRoboKassaResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: BuyWithRoboCassaResponseData,
)

data class BuyWithRoboCassaResponseData(
    @SerializedName("created_time")
    val createdTime: Long,
    @SerializedName("payment_url")
    val paymentUrl: String,
)