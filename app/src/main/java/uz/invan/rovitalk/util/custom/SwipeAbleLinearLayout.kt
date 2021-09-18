package uz.invan.rovitalk.util.custom

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import uz.invan.rovitalk.util.ktx.RoviTimer
import uz.invan.rovitalk.util.ktx.doOnEnd

@SuppressLint("ClickableViewAccessibility")
class SwipeAbleLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val translateBy = 16f
    private val duration = 2000L
    private var listener: SwipeAbleListener? = null
    private var timer: CountDownTimer? = null
    private var isOpen = false

    init {
        translationY = +translateBy
        show()
    }

    private fun show() {
        if (isOpen) {
            setTimer()
            return
        }
        listener?.onShow()
//        setBackgroundResource(R.drawable.background_swipe_able)
        animate().translationY(-translateBy).setDuration(ENTER_ANIMATION_DURATION).doOnEnd {
            isOpen = true
            setTimer()
        }.start()
    }

    private fun setTimer() {
        timer?.cancel()
        timer = RoviTimer(duration) {
            dismiss()
        }
        timer?.start()
    }

    private fun dismiss() {
        isOpen = false
        listener?.onDismiss()
        setBackgroundResource(0)
        animate().translationY(+translateBy).setDuration(EXIT_ANIMATION_DURATION).doOnEnd {
        }.start()
    }

    fun onScrollStart() {
//        show()
//        timer?.cancel()
    }

    fun onScrollFinish() {
//        Timber.d("scroll finish")
//        setTimer()
    }

    interface SwipeAbleListener {
        fun onShow()
        fun onDismiss()
    }

    fun listen(
        onShow: () -> Unit = {},
        onDismiss: () -> Unit = {},
    ) {
        listener = object : SwipeAbleListener {
            override fun onShow() {
                onShow.invoke()
            }

            override fun onDismiss() {
                onDismiss.invoke()
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        show()
        return !isOpen
    }

    companion object {
        private const val ENTER_ANIMATION_DURATION = 225L
        private const val EXIT_ANIMATION_DURATION = 175L
    }
}