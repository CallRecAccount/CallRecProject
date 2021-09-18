package uz.invan.rovitalk.util.custom

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import uz.invan.rovitalk.R
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class TimerProgressBar
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ProgressBar(context, attrs, defStyle) {
    private val isLoading = AtomicBoolean(false)
    private var timer: CountDownTimer? = null
    private var listener: ProgressEnd? = null

    init {
        isIndeterminate = false
        progressDrawable = ContextCompat.getDrawable(context, R.drawable.timer_progress_drawable)
        max = MAX_PROGRESS
    }

    fun start(time: Long, textView: TextView, end: ProgressEnd) {
        if (isLoading.compareAndSet(true, false)) {
            timer?.cancel()
        }
        if (time < INTERVAL) return

        listener = end
        max = ((time / INTERVAL).toInt())

        timer = object : CountDownTimer(time, INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                val remain = (millisUntilFinished / INTERVAL).toInt()
                progress = remain

                textView.isVisible = true

                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                            millisUntilFinished))

                textView.text = context.getString(R.string.x_minute_seconds, minutes, seconds)
            }

            override fun onFinish() {
                textView.isGone = true

                isLoading.set(false)
                listener?.setProgressEnd()
            }
        }.start()
        isLoading.set(true)
    }

    @FunctionalInterface
    fun interface ProgressEnd {
        fun setProgressEnd()
    }

    companion object {
        const val MAX_PROGRESS = 100
        const val INTERVAL = 1000L
    }
}