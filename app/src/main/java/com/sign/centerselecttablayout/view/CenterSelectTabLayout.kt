package com.sign.centerselecttablayout.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.sign.centerselecttablayout.adapter.CenterSelectTabLayoutAdapter
import com.sign.centerselecttablayout.util.getScreenWidth

/**
 * Created by CaoYongSheng
 * on 2020/5/16
 *
 * @author admin
 */
private const val CAN_IGNORE_OFFSET = 2f

class CenterSelectTabLayout(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    var layoutMangerReverse = false
        set(value) {
            field = value
            centerSelectLayoutManager.reverseLayout = layoutMangerReverse
        }
    var preHighLightPosition = -1
    var targetPosition = -1
    var totalOffsetX = 0f
    var centerX = 0
    var centerSelectLayoutManager: CenterSelectLayoutManager
    var centerSelectTabLayoutListener: CenterSelectTabLayoutAdapter.CenterSelectTabLayoutListener? = null
    var centerSelectTabLayoutAdapter: CenterSelectTabLayoutAdapter? = null

    init {
        centerX = getScreenWidth() / 2
        centerSelectLayoutManager = CenterSelectLayoutManager(context, layoutMangerReverse)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalOffsetX += dx
                if (dx != 0) {
                    //如果用户打断滚动 将目标滚动下标重置
                    if (targetPosition != -1) {
                        if (scrollState == SCROLL_STATE_DRAGGING) {
                            targetPosition = -1
                        }
                    }
                    val firstVisibleItemPosition =
                        centerSelectLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition =
                        centerSelectLayoutManager.findLastVisibleItemPosition()
                    if (centerSelectTabLayoutAdapter != null) {
                        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                            val viewByPosition = centerSelectLayoutManager.findViewByPosition(i)
                            if (viewByPosition != null && viewByPosition.left <= centerX && viewByPosition.right >= centerX) {
                                centerSelectTabLayoutAdapter!!.highLightPosition = i
                                break
                            }
                        }
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    val firstVisibleItemPosition =
                        centerSelectLayoutManager.findFirstVisibleItemPosition()
                    val lastVisibleItemPosition =
                        centerSelectLayoutManager.findLastVisibleItemPosition()
                    if (targetPosition != -1
                        && targetPosition >= firstVisibleItemPosition
                        && targetPosition <= lastVisibleItemPosition
                    ) {
                        targetPosition = -1
                        centerSelectTabLayoutAdapter?.highLightPosition = targetPosition
                    }
                    if (centerSelectTabLayoutAdapter != null) {
                        val currentHighLightPosition =
                            centerSelectTabLayoutAdapter!!.highLightPosition
                        if (preHighLightPosition != currentHighLightPosition) {
                            val currentHighLightView =
                                centerSelectLayoutManager.findViewByPosition(
                                    currentHighLightPosition
                                )
                            if (currentHighLightView != null) {
                                if ((currentHighLightView.right - currentHighLightView.left) / 2f > CAN_IGNORE_OFFSET) {
                                    smoothScrollToPosition(currentHighLightPosition)
                                }
                            }
                            centerSelectTabLayoutListener?.onTabSelect(currentHighLightPosition)
                        }
                    }
                }
            }
        })
    }

    fun onAdapterItemClick(position: Int) {
        targetPosition = position
        smoothScrollToPosition(position)
    }

    override fun scrollToPosition(position: Int) {
        if (centerSelectTabLayoutAdapter != null && position >= 0 && position < centerSelectTabLayoutAdapter!!.itemCount) {
            preHighLightPosition = position
            val offset = centerX - centerSelectTabLayoutAdapter!!.highLightPosition
            centerSelectLayoutManager.scrollToPositionWithOffset(position, offset)
            val directScrollTotalOffset =
                centerSelectTabLayoutAdapter!!.getDirectScrollTotalOffset()
            if (directScrollTotalOffset > centerX) {
                totalOffsetX = (directScrollTotalOffset - centerX).toFloat()
            }
        }
    }

    override fun smoothScrollToPosition(position: Int) {
        if (centerSelectTabLayoutAdapter != null && position >= 0 && position < centerSelectTabLayoutAdapter!!.itemCount) {
            preHighLightPosition = position
            super.smoothScrollToPosition(position)
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        if (layout !is CenterSelectLayoutManager) {
            throw IllegalArgumentException("LayoutManager must be CenterSelectLayoutManager")
        } else {
            super.setLayoutManager(layout)
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter !is CenterSelectTabLayoutAdapter) {
            throw IllegalArgumentException("adapter manager must be CenterSelectTabLayoutAdapter")
        } else {
            layoutManager = centerSelectLayoutManager
            super.setAdapter(adapter)
        }
    }

    inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            //首部的几个item可能无法滚动到recyclerView中心位置
            return if (totalOffsetX + viewEnd < boxStart + (boxEnd - boxStart) / 2) {
                preHighLightPosition = -1
                0
            } else {
                boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
    }

    inner class CenterSelectLayoutManager(context: Context, reverseLayout: Boolean) :
        LinearLayoutManager(context, HORIZONTAL, reverseLayout) {

        private val smoothScroller: CenterSmoothScroller = CenterSmoothScroller(context)

        override fun smoothScrollToPosition(
            recyclerView: RecyclerView,
            state: State,
            position: Int
        ) {
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }

    }
}
