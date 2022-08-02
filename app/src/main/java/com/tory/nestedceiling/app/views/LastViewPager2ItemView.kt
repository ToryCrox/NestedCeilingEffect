package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.page.*
import com.tory.nestedceiling.widget.NestedCeilingHelper
import java.util.ArrayList

class LastViewPager2Model

class LastViewPager2ItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<LastViewPager2Model>(context, attrs) {

    val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
    val viewPager = findViewById<ViewPager2>(R.id.view_pager)

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        NestedCeilingHelper.setNestedChildContainerTag(this)

        val pagerAdapter = ViewPagerAdapter(context as FragmentActivity, getPageFragments())
        viewPager.adapter = pagerAdapter

        val labels = arrayOf("精选", "直播", "新品推荐", "Empty", "Empty", "Empty", "Empty", "Empty", "Empty", "Empty", "Empty" )
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()
    }

    private fun getPageFragments(): List<Fragment> {
        val data: MutableList<Fragment> = ArrayList()
        data.add(StaggeredFragment())
        data.add(StaggeredFragment())
        data.add(LinearINormalFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        data.add(EmptyFragment())
        return data
    }


    override fun getLayoutId(): Int = R.layout.item_last_view_pager2

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, NestedCeilingHelper.wrapContainerMeasureHeight(this, heightMeasureSpec))
    }


    private class ViewPagerAdapter(fragmentActivity: FragmentActivity,
        private val data: List<Fragment>)
        : FragmentStateAdapter(fragmentActivity) {

        override fun createFragment(position: Int): Fragment {
            return data[position]
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }
}
