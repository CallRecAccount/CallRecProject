package uz.invan.rovitalk.util.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.warkiz.widget.IndicatorSeekBar
import es.dmoral.toasty.Toasty
import io.feeeei.circleseekbar.CircleSeekBar
import timber.log.Timber
import uz.invan.rovitalk.R
import java.text.DecimalFormat
import java.text.NumberFormat


// Layout-ktx
val ViewGroup.layoutInflater: LayoutInflater
    get() {
        return LayoutInflater.from(context)
    }

fun ViewGroup.inflate(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false)

fun ConstraintLayout.setDimensionRatioTo(view: View, ratio: String) {
    val set = ConstraintSet()
    set.clone(this)
    set.setDimensionRatio(view.id, ratio)
    set.applyTo(this)
}

// measurements-ktx
fun Context.px(dp: Float) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

fun Context.px(dp: Int) = px(dp.toFloat()).toInt()

fun View.px(dp: Float) = context.px(dp)

fun View.px(dp: Int) = px(dp.toFloat()).toInt()

fun Fragment.px(dp: Float) = requireContext().px(dp)

fun Fragment.px(dp: Int) = requireContext().px(dp)

// component-ktx
val Fragment.roviActivity: AppCompatActivity
    get() {
        return requireActivity() as AppCompatActivity
    }

val Fragment.roviContext: Context
    get() {
        return requireContext()
    }

// component-soft-keyboard-ktx
fun Activity.hideSoftKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideSoftKeyboard() = roviActivity.hideSoftKeyboard()

fun Activity.showSoftKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun Fragment.showSoftKeyboard() = roviActivity.showSoftKeyboard()

// view-fields-ktx
var View.roviEnable: Boolean
    set(value) {
        alpha = if (value) 1f else 0.5f
        isEnabled = value
    }
    get() = isEnabled

var CircleSeekBar.roviEnable: Boolean
    @SuppressLint("ClickableViewAccessibility")
    set(value) {
        setOnTouchListener { _, _ ->
            return@setOnTouchListener !value
        }
        alpha = if (value) 1f else 0.5f
    }
    get() = isEnabled

var IndicatorSeekBar.roviEnable: Boolean
    set(value) {
        setUserSeekAble(value)
        alpha = if (value) 1f else 0.5f
    }
    get() = alpha == 1f

val EditText.stringText: String
    get() {
        return text.toString()
    }

// bitmap-ktx
fun Context.toBitmap(@DrawableRes resId: Int): Bitmap? {
    return try {
        BitmapFactory.decodeResource(resources, resId)
    } catch (exception: Exception) {
        Timber.d(exception)
        return null
    }
}

fun Fragment.toBitmap(@DrawableRes resId: Int) = context?.toBitmap(resId)

// toasty-ktx
private var currentRoviToast: Toast? = null
fun Context.roviError(message: String) {
    currentRoviToast?.cancel()
    currentRoviToast = Toasty.error(this, message, Toast.LENGTH_SHORT, false)
    currentRoviToast?.show()
}

fun Fragment.roviError(message: String) = roviContext.roviError(message)

fun Context.roviSuccess(message: String) {
    currentRoviToast?.cancel()
    currentRoviToast = Toasty.success(this, message, Toast.LENGTH_SHORT, false)
    currentRoviToast?.show()
}

fun Fragment.roviSuccess(message: String) = roviContext.roviSuccess(message)

// phone-formatter-ktx
fun String?.phone(): String? {
    if (this == null) return null

    return "+$this"
/*    val str = removeRange(0,
        "+998 " + "(" + str.substring(0, 2) + ") " + str.substring(2, 9)
    } catch (exception: Exception) {
        exception.printStackTrace()
        this
    }*/
}

// money-ktx
fun Int?.money(): String? {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        val symbols = (formatter as DecimalFormat).decimalFormatSymbols
        symbols.currencySymbol = ""
        formatter.decimalFormatSymbols = symbols
        formatter.maximumFractionDigits = 0
        return formatter.format(this).trim()
    } catch (exception: Exception) {
        exception.printStackTrace()
        toString()
    }
}

fun Double?.money(): String? {
    return try {
        val formatter = NumberFormat.getCurrencyInstance()
        val symbols = (formatter as DecimalFormat).decimalFormatSymbols
        symbols.currencySymbol = ""
        formatter.decimalFormatSymbols = symbols
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(this).trim()
    } catch (exception: Exception) {
        exception.printStackTrace()
        toString()
    }
}

// pad-start-ktx
fun Int.pad(): String {
    return toString().padStart(2, '0')
}

// bottom-sheet-ktx
fun BottomSheetDialog.setupFullHeight(activity: Activity?) {
    val bottomSheet: FrameLayout = findViewById(R.id.design_bottom_sheet) ?: return
    val behavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(bottomSheet)
    val layoutParams: ViewGroup.LayoutParams = bottomSheet.layoutParams
    val windowHeight = getWindowHeight(activity)
    layoutParams.height = windowHeight
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
}

private fun getWindowHeight(activity: Activity?): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        activity?.display?.getRealMetrics(displayMetrics)
    else
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}