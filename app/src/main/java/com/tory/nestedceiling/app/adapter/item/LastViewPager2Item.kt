package com.tory.nestedceiling.app.adapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.model.LastViewPager2
import com.tory.nestedceiling.app.page.LinearINormalFragment
import com.tory.nestedceiling.app.page.StaggeredFragment
import com.drakeet.multitype.ItemViewBinder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.tory.nestedceiling.widget.*
import java.util.*

class LastViewPager2Item(private val recyclerView: NestedParentRecyclerView) : ItemViewBinder<LastViewPager2, LastViewPager2Item.ViewHolder>() {

    class ViewHolder(itemView: View, recyclerView: NestedParentRecyclerView) : RecyclerView.ViewHolder(itemView) {

        init {
            val viewPager: ViewPager2 = itemView.findViewById(R.id.view_pager)
            viewPager.offscreenPageLimit = 2
            val tabLayout: TabLayout = itemView.findViewById(R.id.tab_layout)

            val pagerAdapter = ViewPagerAdapter(itemView.context as FragmentActivity, getPageFragments())
            viewPager.adapter = pagerAdapter
            val labels = arrayOf("精选", "直播", "新品推荐")
            TabLayoutMediator(tabLayout, viewPager, TabConfigurationStrategy { tab, position ->
                tab.text = labels[position]
            }).attach()
            recyclerView.addOnChildAttachStateListener(object : OnChildAttachStateListener {
                override fun onChildDetachedFromTop() {
                    pagerAdapter.notifyChildToTop()
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
            return data
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LastViewPager2) {

    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_last_view_pager2, parent, false).apply {
             NestedCeilingHelper.setNestedChildContainerTag(this)
        }, recyclerView)
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val data: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return data[position]
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun notifyChildToTop() {
            for (i in data.indices){
                val fragment = data[i]
                if(fragment is LinearINormalFragment){
                    fragment.resetToTop()
                } else if(fragment is StaggeredFragment) {
                    fragment.resetToTop()
                }
            }
        }

    }

}