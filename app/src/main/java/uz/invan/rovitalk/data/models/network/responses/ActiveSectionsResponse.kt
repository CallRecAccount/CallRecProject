package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class ActiveSectionsResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<ActiveSectionData>
)

data class ActiveSectionData(
    @SerializedName("bought_date")
    val boughtDate: Long,
    @SerializedName("expire_date")
    val expireDate: Long,
    @SerializedName("_id")
    val id: String,
    @SerializedName("section")
    val section: String,
    @SerializedName("section_title")
    val sectionTitle: String
)