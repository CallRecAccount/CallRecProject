package uz.invan.rovitalk.util.ktx

import android.animation.Animator
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import com.yarolegovich.discretescrollview.DiscreteScrollView
import douglasspgyn.com.github.circularcountdown.CircularCountdown
import douglasspgyn.com.github.circularcountdown.listener.CircularListener
import uz.invan.rovitalk.ui.base.BaseVH

// animation-ktx
fun ViewPropertyAnimator.doOnEnd(onEnd: () -> Unit): ViewPropertyAnimator {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {

        }

        override fun onAnimationEnd(p0: Animator?) {
            onEnd.invoke()
        }

        override fun onAnimationCancel(p0: Animator?) {

        }

        override fun onAnimationStart(p0: Animator?) {

        }

    }
    setListener(listener)

    return this
}

fun Animation?.doOnEnd(onEnd: () -> Unit) = object : Animation.AnimationListener {
    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        onEnd.invoke()
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }
}

// indicator-seek-bar-ktx
fun IndicatorSeekBar.setOnSeekingListener(onSeeking: (Int) -> Unit): OnSeekChangeListener? {
    onSeekChangeListener = object : OnSeekChangeListener {
        override fun onSeeking(seekParams: SeekParams?) {
            if (seekParams?.progress == null) return
            val progress = seekParams.progress
            onSeeking.invoke(progress)
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
        }
    }
    return onSeekChangeListener
}

// glide-ktx
fun RequestBuilder<Drawable>.into(textView: TextView): CustomTarget<Drawable> {
    return into(object : CustomTarget<Drawable>() {
        override fun onResourceReady(
            resource: Drawable,
            transition: Transition<in Drawable>?,
        ) {
            textView.background = resource
        }

        override fun onLoadCleared(placeholder: Drawable?) {

        }
    })
}

// bottom-sheet-ktx
private val stateChangeListeners = arrayListOf<StateChangeListener>()

fun <T : View?> BottomSheetBehavior<T>.addStateChangeListener(onStateChange: (newState: Int) -> Unit) {
    stateChangeListeners.add(onStateChange)
    return addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(p0: View, p1: Int) {
            for (listener in stateChangeListeners) listener.onStateChanged(p1)
        }

        override fun onSlide(p0: View, p1: Float) {

        }
    })
}

fun interface StateChangeListener {
    fun onStateChanged(newState: Int)
}

// timer-ktx
fun CircularCountdown.startTimer(timeInMillis: Long, onEnd: () -> Unit): CircularCountdown {
    return create(
        pastTime = 1,
        endTime = (timeInMillis / 1000).toInt(),
        timeType = CircularCountdown.TYPE_SECOND
    ).listener(object : CircularListener {
        override fun onFinish(newCycle: Boolean, cycleCount: Int) {
            onEnd.invoke()
        }

        override fun onTick(progress: Int) {
//            if (progress % 2 == 0) setProgressForegroundColor(ContextCompat.getColor(Rovi.app(),
//                R.color.deluge))
//            else setProgressForegroundColor(ContextCompat.getColor(Rovi.app(), R.color.victoria))
        }

    }).start()
}

class RoviTimer(millisInFuture: Long, private val onFinish: () -> Unit) :
    CountDownTimer(millisInFuture, 1000) {
    override fun onTick(millisUntilFinished: Long) {

    }

    override fun onFinish() {
        onFinish.invoke()
    }
}

fun startTimer(millisInFuture: Long, onFinish: () -> Unit) {
    object : CountDownTimer(millisInFuture, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            onFinish.invoke()
        }
    }
}

// viewpager2-ktx
fun ViewPager2.setOnPageSelected(onPageSelected: (Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }
    })
}

// fun discrete-rv-ktx
fun <T> DiscreteScrollView.addScrollStateChangeListener(
    onScrollStart: () -> Unit,
    onScrollEnd: () -> Unit,
) {
    addScrollStateChangeListener(object : DiscreteScrollView.ScrollStateChangeListener<BaseVH<T>> {
        override fun onScrollStart(currentItemHolder: BaseVH<T>, adapterPosition: Int) {
            onScrollStart.invoke()
        }

        override fun onScrollEnd(currentItemHolder: BaseVH<T>, adapterPosition: Int) {
            onScrollEnd.invoke()
        }

        override fun onScroll(
            scrollPosition: Float,
            currentPosition: Int,
            newPosition: Int,
            currentHolder: BaseVH<T>?,
            newCurrent: BaseVH<T>?,
        ) {

        }

    })
}