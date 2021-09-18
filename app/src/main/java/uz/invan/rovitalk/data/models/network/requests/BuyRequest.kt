package uz.invan.rovitalk.data.models.network.requests


import com.google.gson.annotations.SerializedName

data class BuyRequest(
    @SerializedName("indexes")
    val prices: List<String>,
)