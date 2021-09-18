package uz.invan.rovitalk.data.models.network.responses


import com.google.gson.annotations.SerializedName

data class UploadFileResponse(
    @SerializedName("statusCode")
    val code: Int,
    @SerializedName("data")
    val data: UploadFileData,
)

data class UploadFileData(
    @SerializedName("path")
    val path: String,
)