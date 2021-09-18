package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class SectionsResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<SectionResponseData>,
)


data class SectionResponseData(
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_main")
    val isMain: Boolean,
    @SerializedName("title")
    val title: String,
)