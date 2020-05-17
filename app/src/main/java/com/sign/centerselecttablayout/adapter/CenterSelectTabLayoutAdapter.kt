package com.sign.centerselecttablayout.adapter

import androidx.recyclerview.widget.RecyclerView
import com.sign.centerselecttablayout.view.CenterSelectTabLayout

/**
 * Created by CaoYongSheng
 * on 2020/5/16
 *
 * @author admin
 */
abstract class CenterSelectTabLayoutAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var centerSelectTabLayoutListener: CenterSelectTabLayoutListener? = null
    var highLightPosition = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    abstract fun getHalfOfSelectTabWidth(): Int

    abstract fun getDirectScrollTotalOffset(): Int

    open class CenterSelectTabLayoutListener(
        private val centerSelectTabLayout: CenterSelectTabLayout,
        private val centerSelectTabLayoutAdapter: CenterSelectTabLayoutAdapter
    ) {
        init {
            centerSelectTabLayout.centerSelectTabLayoutListener = this
        }

        open fun onTabSelect(position: Int) {
            if (centerSelectTabLayoutAdapter.highLightPosition != position) {
                centerSelectTabLayout.onAdapterItemClick(position)
            }
        }
    }
}