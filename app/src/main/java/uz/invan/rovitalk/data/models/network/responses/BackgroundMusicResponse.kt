package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class BackgroundMusicResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<BackgroundMusicData>
)

data class BackgroundMusicData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("link")
    val link: String
)