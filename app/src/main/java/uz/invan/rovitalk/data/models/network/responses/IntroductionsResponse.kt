package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class IntroductionsResponse(
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("data")
    val data: List<IntroductionData>,
)

data class IntroductionData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("link")
    val link: String,
)