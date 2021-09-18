package uz.invan.rovitalk.data.models.network.responses

import com.google.gson.annotations.SerializedName

data class SubCategoriesResponse(
    val time: Long,
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: SubCategoriesResponseData,
)

data class SubCategoriesResponseData(
    @SerializedName("bought_date")
    val boughtDate: Long?,
    @SerializedName("categories")
    val subCategories: List<SubCategoriesData>,
)

data class SubCategoriesData(
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("section_title")
    val sectionTitle: String,
    @SerializedName("category_id")
    val category: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("ui_type")
    val type: String,
    @SerializedName("purchase_type")
    val purchaseType: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("header")
    val header: String?,
    @SerializedName("sub_header")
    val subHeader: String?,
    @SerializedName("count")
    val count: Int,
    @SerializedName("is_banner")
    val isBanner: Boolean,
    @SerializedName("opening_day")
    val openingDay: Int,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("updated_at")
    val updatedAt: Long,
)