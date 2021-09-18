package uz.invan.rovitalk.util.ktx

import android.animation.LayoutTransition
import android.view.ViewGroup

// layout-ktx
fun ViewGroup.roviAnimateLayoutChanges() {
    val transition = LayoutTransition()
    transition.setAnimateParentHierarchy(false)
    layoutTransition = transition
}