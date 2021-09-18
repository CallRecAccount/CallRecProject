package uz.invan.rovitalk.util.custom.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.FloatRange
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class ItemFeedPodcastsGridItemDecoration(@FloatRange(from = 0.0) val margin: Float) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildLayoutPosition(view)
        if (position % 2 == 0)
            outRect.right = (margin / 2).roundToInt()
        else
            outRect.left = (margin / 2).roundToInt()
        outRect.bottom = (margin * 2).roundToInt()
    }
}