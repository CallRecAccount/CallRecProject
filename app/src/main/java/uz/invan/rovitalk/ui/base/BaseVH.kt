package uz.invan.rovitalk.ui.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseVH<T>(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
    val context = view.context
    var isInitialized = AtomicBoolean(false)
//    var isInitialized = false

    open fun onInit(item: T) {}

    @CallSuper
    open fun onBind(item: T) {
        if (isInitialized.compareAndSet(false, true)) onInit(item)
    }

    override val containerView: View?
        get() = itemView
}