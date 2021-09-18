package uz.invan.rovitalk.data.models.network.requests

import com.google.gson.annotations.SerializedName

data class SubCategoriesRequest(
    @SerializedName("category_id")
    val categoryId: String,
)