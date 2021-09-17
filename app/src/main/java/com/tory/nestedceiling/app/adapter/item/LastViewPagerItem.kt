package com.tory.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.model.LastViewPager
import com.tory.nestedceiling.app.page.*
import com.tory.nestedceiling.widget.NestedParentRecyclerView
import com.tory.nestedceiling.widget.OnChildAttachStateListener
import com.drakeet.multitype.ItemViewBinder
import com.google.android.material.tabs.TabLayout
import java.util.*

class LastViewPagerItem(private val recyclerView: NestedParentRecyclerView) : ItemViewBinder<LastViewPager, LastViewPagerItem.ViewHolder>() {

    class ViewHolder(itemView: View, recyclerView: NestedParentRecyclerView) : RecyclerView.ViewHolder(itemView) {

        init {
            val viewPager: ViewPager = itemView.findViewById(R.id.view_pager)
            val tabLayout: TabLayout = itemView.findViewById(R.id.tab_layout)

            val labels = arrayOf("精选", "直播", "新品推荐", "empty")
            val pagerAdapter = ViewPagerAdapter(itemView.context as FragmentActivity, getPageFragments(), labels)
            viewPager.adapter = pagerAdapter

            tabLayout.setupWithViewPager(viewPager)
            recyclerView.addOnChildAttachStateListener(object : OnChildAttachStateListener {
                override fun onChildDetachedFromTop() {

                }

                override fun onChildAttachedToTop() {
                }

            })
        }

        private fun getPageFragments(): List<Fragment> {
            val data: MutableList<Fragment> = ArrayList()
            data.add(StaggeredFragment())
            data.add(StaggeredFragment())
            data.add(LinearINormalFragment())
            data.add(EmptyFragment())
            return data
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LastViewPager) {

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_last_view_pager, parent, false).apply {
             setTag(R.id.nested_child_item_container, true)
        }, recyclerView)
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