package uz.invan.rovitalk.util.ktx

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.FeedSubCategory
import uz.invan.rovitalk.data.models.network.responses.RoviErrorResponseData

private data class BaseResponse(
    @SerializedName("statusCode")
    val code: Int,
    val time: Long,
)

fun ResponseBody.roviTime(): Long {
    Timber.tag("CUSTOM").d("String: ${string()}")
    return Gson().fromJson(string(), BaseResponse::class.java).time
}

fun HttpException.toRoviError(): RoviErrorResponseData? {
    try {
        response()?.errorBody()?.charStream()?.let {
            return Gson().fromJson(it, RoviErrorResponseData::class.java)
        }
        return null
    } catch (exception: Exception) {
        return null
    }
}

fun String.separateFromBase(): String {
    return substringAfter(BuildConfig.ROVI_URL)
}

// category-sub-to-category
fun FeedSubCategory.category() = FeedCategory(
    id = id,
    title = title,
    image = image,
    duration = duration,
    section = "",
    sectionTitle = sectionTitle,
    type = type,
    purchaseType = purchaseType,
    count = count,
    description = description,
    header = header,
    subHeader = subHeader,
    subCategories = emptyList(),
    mostListened = mostListened,
    isBanner = isBanner,
    isActive = isActive,
    lastListenedAt = lastListenedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)