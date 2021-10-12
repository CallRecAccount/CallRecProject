package uz.invan.rovitalk.util.ktx

import android.os.Build
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import uz.invan.rovitalk.BuildConfig
import uz.invan.rovitalk.data.models.feed.FeedCategory
import uz.invan.rovitalk.data.models.feed.FeedSubCategory
import uz.invan.rovitalk.data.models.network.responses.RoviErrorResponseData
import java.io.File
import java.io.FileOutputStream
import java.net.URL

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

fun String.saveTo(file: File, scope: CoroutineScope, block: () -> Unit) {
//    val executor = Executors.newSingleThreadExecutor()
//    val handler = Handler(Looper.getMainLooper())
    scope.launch(Dispatchers.IO) {
        val url = URL(this@saveTo)
        val connection = url.openConnection()
        connection.connect()
        val length = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) connection.contentLengthLong else connection.contentLength.toLong()
        Timber.tag("AAA length").d(length.toString())
        url.openStream().use { input ->
            if (!file.exists()) {
                file.createNewFile()
            }
            try {
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            } catch (e: Exception) {
                Timber.e(e)
            } finally {
                withContext(Dispatchers.Main) {
                    block()
                }
            }
        }

    }
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