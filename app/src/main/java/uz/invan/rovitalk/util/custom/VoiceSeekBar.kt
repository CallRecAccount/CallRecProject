package uz.invan.rovitalk.util.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import uz.invan.rovitalk.R

@Deprecated("For some time deprecated")
class VoiceSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {
    private val paint = Paint()
    private val path = Path()

    override fun onDraw(canvas: Canvas?) {
        paint.color = ContextCompat.getColor(context, R.color.abbey)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL

        val startX = x
        val startY = height.toFloat()
        val endX = x + width
        val endY = height.toFloat()
        path.moveTo(startX, startY)
        path.lineTo(endX, endY)
        path.lineTo(endX, endY - height)
        path.lineTo(startX, startY)
        path.close()

        canvas?.drawPath(path, paint)
    }
}