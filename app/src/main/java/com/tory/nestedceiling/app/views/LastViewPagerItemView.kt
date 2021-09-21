package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.page.*
import com.tory.nestedceiling.widget.NestedCeilingHelper
import java.util.ArrayList

class LastViewPagerModel

class LastViewPagerItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<LastViewPagerModel>(context, attrs) {

    val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
    val viewPager = findViewById<ViewPager>(R.id.view_pager)

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        NestedCeilingHelper.setNestedChildContainerTag(this)

        val labels = arrayOf("精选", "直播", "新品推荐", "empty")
        val pagerAdapter = ViewPagerAdapter(context as FragmentActivity, getPageFragments(), labels)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }

    private fun getPageFragments(): List<Fragment> {
        val data: MutableList<Fragment> = ArrayList()
        data.add(StaggeredFragment())
        data.add(StaggeredFragment())
        data.add(LinearINormalFragment())
        data.add(EmptyFragment())
        return data
    }


    override fun getLayoutId(): Int = R.layout.item_last_view_pager

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, NestedCeilingHelper.wrapContainerMeasureHeight(this, heightMeasureSpec))
    }


    private class ViewPagerAdapter(fragmentActivity: FragmentActivity,
        private val data: List<Fragment>,
        private val labels: Array<String>)
        : FragmentStatePagerAdapter(fragmentActivity.supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getPageTitle(position: Int): CharSequence {
            return labels[position]
        }

        override fun getCount(): Int = data.size

        override fun getItem(position: Int): Fragment = data[position]
    }
}