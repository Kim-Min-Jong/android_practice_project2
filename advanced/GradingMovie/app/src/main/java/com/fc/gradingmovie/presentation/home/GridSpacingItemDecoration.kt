package com.fc.gradingmovie.presentation.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int
) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapterPosition = parent.getChildAdapterPosition(view)
        val gridLayoutManager = parent.layoutManager as GridLayoutManager
        // 리사이클러뷰의 각 줄마다의 spanSize를 가져와서
        val spanSize = gridLayoutManager.spanSizeLookup.getSpanSize(adapterPosition)

        // spanSize가 1칸(합쳐진것) 일때는 일정 여백을 상하좌우에 줌
        if (spanSize == spanCount) {
            outRect.left = spacing
            outRect.right = spacing
            outRect.top = spacing
            outRect.bottom = spacing
            return
        }

        // 합쳐진 것이 아닌 3개 짜리는 각 아이템마다 같은 여백을 주면 아이템 중간 중간 영역의 여백이 합쳐져
        // 가장자리의 2배의 여백이 된다. 이것을 방지하기 위해 별도의 계산

        // 아이템의 가장자리를 찾기위해 인덱스를 찾아옴 (span중에 어떤 위치에 있는지 가져옴)
        /*  ex) 행당 3개의 아이템
        0 1 2            0 1 2
        3 4 5            0 1 2
        의 형태가 아니라    줄마다 같은 번호를 반복해서 가져와 가장자리를 알 수 있게함
         */
       val column = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
        val itemHorizontalSpacing = ((spanCount + 1) * spacing) / spanCount.toFloat()
        when (column) {
            // 왼쪽 가장자리
            0 -> {
                outRect.left = spacing
                outRect.right = (itemHorizontalSpacing - spacing).toInt()
            }
            // 오른쪽 가장자리
            (spanCount - 1) -> {
                outRect.left = (itemHorizontalSpacing - spacing).toInt()
                outRect.right = spacing
            }
            // 중간
            else -> {
                outRect.left = (itemHorizontalSpacing / 2).toInt()
                outRect.right = (itemHorizontalSpacing / 2).toInt()
            }
        }
        // 위아래는 그대로
        outRect.top = spacing
        outRect.bottom = spacing
    }
}