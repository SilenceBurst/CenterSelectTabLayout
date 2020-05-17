package com.sign.centerselecttablayout

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sign.centerselecttablayout.adapter.CenterSelectTabLayoutAdapter
import com.sign.centerselecttablayout.util.dp
import com.sign.centerselecttablayout.util.sp
import com.sign.centerselecttablayout.view.CenterSelectTabLayout

val items = listOf(
    "C",
    "Java",
    "Python",
    "C++",
    "C#",
    "Visual Basic",
    "JavaScript",
    "PHP",
    "Swift",
    "Go",
    "Ruby",
    "Dart",
    "Kotlin",
    "Groovy"
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cstlCategory = findViewById<CenterSelectTabLayout>(R.id.cstl_category)
        val tvClickHistory = findViewById<TextView>(R.id.tv_click_history)
        val exampleAdapter = ExampleAdapter()
        exampleAdapter.centerSelectTabLayoutListener = object :
            CenterSelectTabLayoutAdapter.CenterSelectTabLayoutListener(
                cstlCategory,
                exampleAdapter
            ) {
            override fun onTabSelect(position: Int) {
                super.onTabSelect(position)
                tvClickHistory.append(items[position])
                tvClickHistory.append("     ")
            }
        }
        cstlCategory.adapter = exampleAdapter
        cstlCategory.smoothScrollToPosition(10)
    }

    class ExampleAdapter :
        CenterSelectTabLayoutAdapter() {

        private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            paint.textSize = 16f.sp
        }

        override fun getHalfOfSelectTabWidth(): Int {
            paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            val selectLabelWidth = paint.measureText(items[highLightPosition])
            return (selectLabelWidth / 2).toInt()
        }

        override fun getDirectScrollTotalOffset(): Int {
            paint.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            var totalOffsetXTemp = 0
            for (i in 0 until highLightPosition) {
                totalOffsetXTemp += (20f.dp + paint.measureText(items[i])).toInt()
            }
            paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            val selectLabelWidth = paint.measureText(items[highLightPosition]).toInt()
            totalOffsetXTemp += selectLabelWidth / 2
            return totalOffsetXTemp
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false)
            return object : RecyclerView.ViewHolder(view) {}
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemView: TextView = holder.itemView as TextView
            itemView.text = items[position]
            itemView.paint.typeface =
                Typeface.defaultFromStyle(if (position == highLightPosition) Typeface.BOLD else Typeface.NORMAL)
            holder.itemView.setOnClickListener { centerSelectTabLayoutListener?.onTabSelect(position) }
        }

    }
}
