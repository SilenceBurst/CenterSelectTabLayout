# CenterSelectTabLayout

**Screens**
![](https://github.com/SilenceBurst/CenterSelectTabLayout/blob/master/gif/example.gif)

**Features**
- auto select center tab when scrolling
- after scroll, select tab in the center of layout

**Usage**

- init view
```
<com.sign.centerselecttablayout.view.CenterSelectTabLayout
    android:id="@+id/centerSelectTabLayout"
    android:layout_width="match_parent"
    android:layout_height="60dp" />

val exampleAdapter = ExampleAdapter()
centerSelectLayout.adapter = exampleAdapter
```

- init adapter ( you should implement two function, because may be you will call centerSelectLayout.scrollToPosition() )
```
/**
 * Return half of select Tab width px
 */
abstract fun getHalfOfSelectTabWidth(): Int
/**
 * Return total offset of scrollTo the position
 */
abstract fun getDirectScrollTotalOffset(): Int
```

- init listener ( set CenterSelectTabLayoutListener refresh data when tab select )
```
exampleAdapter.centerSelectTabLayoutListener = object :
    CenterSelectTabLayoutAdapter.CenterSelectTabLayoutListener(
        centerSelectLayout,
        exampleAdapter
    ) {
    override fun onTabSelect(position: Int) {
        super.onTabSelect(position)

    }
}
```

- scrollToPosition or smoothScrollToPosition ( after load data or other case need scroll or smoothScroll to position )
```
centerSelectLayout.smoothScrollToPosition(10)
```

**Contributing**

Yes:) If you found a bug, have an idea how to improve library or have a question, please create new issue or comment existing one. If you would like to contribute code fork the repository and send a pull request.

**Email**

3328019207@qq.com
