package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class FileResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: FileData,
)

data class FileData(
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
    @SerializedName("link")
    val link: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("opening_time")
    val openingTime: Long?,
    @SerializedName("author")
    val author: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("updated_at")
    val updatedAt: Long,
)

enum class FileType(val type: String) {
    AUDIO("audio"), VIDEO("video"), FILE("file"), IMAGE("image")
}