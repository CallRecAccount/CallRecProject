package uz.invan.rovitalk.util.custom

import android.os.SystemClock
import android.view.View
import java.util.*


/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * @param minimumIntervalMillis The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
 */
abstract class DebouncedOnClickListener(private val minimumIntervalMillis: Long) :
    View.OnClickListener {
    private val lastClickMap: MutableMap<View, Long>

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    abstract fun onDebouncedClick(v: View)

    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView]
        val currentTimestamp = SystemClock.uptimeMillis()
        lastClickMap[clickedView] = currentTimestamp
        if (previousClickTimestamp == null || Math.abs(currentTimestamp - previousClickTimestamp) > minimumIntervalMillis) {
            onDebouncedClick(clickedView)
        }
    }

    init {
        lastClickMap = WeakHashMap()
    }

    fun interface OnDebounceClickListener {
        fun onDebounceClick(view: View)
    }
}

fun View.setOnDebounceClickListener(onDebounceClickListener: DebouncedOnClickListener.OnDebounceClickListener) {
    return setOnClickListener(object : DebouncedOnClickListener(500) {
        override fun onDebouncedClick(v: View) {
            onDebounceClickListener.onDebounceClick(v)
        }
    })
}