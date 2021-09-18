package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class PricesResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<PricesData>
)

data class PricesData(
    @SerializedName("title")
    val title: String,
    @SerializedName("prices")
    val prices: List<PriceData>
)

data class PriceData(
    @SerializedName("cost")
    val cost: Int,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("_id")
    val id: String,
    @SerializedName("section")
    val section: String
)