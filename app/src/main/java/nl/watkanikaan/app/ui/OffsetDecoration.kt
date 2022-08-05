package nl.watkanikaan.app.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class OffsetDecoration(private val itemOffset: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) return
        val itemCount = state.itemCount

        val isFirstItem = itemPosition == 0
        val isLastItem = itemCount > 0 && itemPosition == itemCount - 1

        when {
            isFirstItem -> outRect.set(0, itemOffset, itemOffset, itemOffset)
            isLastItem -> outRect.set(itemOffset, itemOffset, 0, itemOffset)
            else -> outRect.set(itemOffset, itemOffset, itemOffset, itemOffset)
        }
    }
}