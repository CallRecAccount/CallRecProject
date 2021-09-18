package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class BuyTotalResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: BuyTotalResponseData,
)

data class BuyTotalResponseData(
    @SerializedName("total")
    val total: Double,
    @SerializedName("total_euro")
    val totalEuro: Double,
    @SerializedName("total_ruble")
    val totalRuble: Double,
    @SerializedName("total_usd")
    val totalUsd: Double,
)