package com.sign.centerselecttablayout.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.sign.centerselecttablayout.adapter.CenterSelectTabLayoutAdapter
import kotlin.math.abs

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
    var preScrollToPosition = -1
    var scrollTargetPosition = -1
    var centerX = 0
    var centerSelectLayoutManager: CenterSelectLayoutManager
    var centerSelectTabLayoutListener: CenterSelectTabLayoutAdapter.CenterSelectTabLayoutListener? =
        null
    var centerSelectTabLayoutAdapter: CenterSelectTabLayoutAdapter? = null

    init {
        centerSelectLayoutManager = CenterSelectLayoutManager(context, layoutMangerReverse)
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dx != 0) {
                    if (scrollState == SCROLL_STATE_DRAGGING) {
                        //if user intercept scroll, reset target position
                        if (scrollTargetPosition != -1) {
                            scrollTargetPosition = -1
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
                    if (centerSelectTabLayoutAdapter != null) {
                        val scrollToPosition: Int
                        if (scrollTargetPosition != -1) {
                            scrollToPosition = scrollTargetPosition
                            centerSelectTabLayoutAdapter!!.highLightPosition =
                                scrollTargetPosition
                            scrollTargetPosition = -1
                        } else {
                            scrollToPosition = centerSelectTabLayoutAdapter!!.highLightPosition
                        }
                        val currentHighLightView =
                            centerSelectLayoutManager.findViewByPosition(scrollToPosition)
                        if (currentHighLightView != null) {
                            val currentHighLightViewCenterX = currentHighLightView.left +
                                    (currentHighLightView.right - currentHighLightView.left) / 2
                            if (abs(currentHighLightViewCenterX - centerX) > CAN_IGNORE_OFFSET
                                && ((currentHighLightViewCenterX > centerX
                                        && canScrollHorizontally(1))
                                        || (currentHighLightViewCenterX < centerX
                                        && canScrollHorizontally(-1)))
                            ) {
                                smoothScrollToPosition(scrollToPosition)
                            }
                        }
                        if (scrollToPosition != preScrollToPosition) {
                            preScrollToPosition = scrollToPosition
                            centerSelectTabLayoutListener?.onTabSelect(
                                scrollToPosition
                            )
                        }
                    }
                }
            }
        })
    }

    fun onAdapterItemClick(position: Int) {
        if (position != preScrollToPosition) {
            preScrollToPosition = position
            smoothScrollToPosition(position)
        }
    }

    override fun scrollToPosition(position: Int) {
        if (centerX == 0) {
            post {
                if (centerSelectTabLayoutAdapter != null && position >= 0 && position < centerSelectTabLayoutAdapter!!.itemCount) {
                    val offset = centerX -
                            centerSelectTabLayoutAdapter!!.getHalfOfSelectTabWidth(position)
                    centerSelectLayoutManager.scrollToPositionWithOffset(position, offset)
                    centerSelectTabLayoutAdapter!!.highLightPosition = position
                    centerSelectTabLayoutListener?.onTabSelect(
                        position
                    )
                }
            }
        }
    }

    override fun smoothScrollToPosition(position: Int) {
        if (centerSelectTabLayoutAdapter != null && position >= 0 && position < centerSelectTabLayoutAdapter!!.itemCount) {
            scrollTargetPosition = position
            super.smoothScrollToPosition(position)
        }
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        if (layout !is CenterSelectLayoutManager) {
            throw IllegalArgumentException("LayoutManager must be CenterSelectLayoutManager")
        } else {
            super.setLayoutManager(layout)
            centerSelectLayoutManager = layout
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter !is CenterSelectTabLayoutAdapter) {
            throw IllegalArgumentException("adapter manager must be CenterSelectTabLayoutAdapter")
        } else {
            layoutManager = centerSelectLayoutManager
            super.setAdapter(adapter)
            centerSelectTabLayoutAdapter = adapter
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = left + (right - left) / 2
    }

    inner class CenterSmoothScroller(context: Context) : LinearSmoothScroller(context) {

        override fun calculateDtToFit(
            viewStart: Int,
            viewEnd: Int,
            boxStart: Int,
            boxEnd: Int,
            snapPreference: Int
        ): Int {
            return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
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
