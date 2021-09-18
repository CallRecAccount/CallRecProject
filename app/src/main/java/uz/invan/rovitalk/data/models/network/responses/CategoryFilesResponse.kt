package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class CategoryFilesResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: List<CategoryFileData>,
)

data class CategoryFileData(
    @SerializedName("category")
    val category: String,
    @SerializedName("category_title")
    val categoryTitle: String,
    @SerializedName("duration")
    val duration: Long,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("updated_at")
    val updatedAt: Long,
)