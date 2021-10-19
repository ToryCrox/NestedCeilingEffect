package com.tory.nestedceiling.app.views

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
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

    val activity: AppCompatActivity = context as AppCompatActivity

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        NestedCeilingHelper.setNestedChildContainerTag(this)

        val pagerAdapter = ViewPagerAdapter(context as FragmentActivity, getPageFragments())
        viewPager.adapter = pagerAdapter

        val labels = arrayOf("精选", "直播", "新品推荐", "Empty")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()
        registerSaveState()
    }

    override fun onChanged(model: LastViewPager2Model) {
        super.onChanged(model)
        consumeSaveState()
    }

    private fun getPageFragments(): List<Fragment> {
        val data: MutableList<Fragment> = ArrayList()
        data.add(StaggeredFragment())
        data.add(StaggeredFragment())
        data.add(LinearINormalFragment())
        data.add(EmptyFragment())
        return data
    }

    /**
     *
     */
    fun registerSaveState() {
        val view = this
        activity.savedStateRegistry.registerSavedStateProvider("ss_${view.javaClass.simpleName}") {
            val bundle = Bundle()
            val container = SparseArray<Parcelable>()
            bundle.putSparseParcelableArray("ss_container_${view.javaClass.simpleName}", container)
            view.saveHierarchyState(container)
            bundle
        }
    }

    /**
     * 对应的子view必须有id才能恢复成功
     */
    fun consumeSaveState() {
        val view = this
        val bundle = activity.savedStateRegistry.consumeRestoredStateForKey("ss_${view.javaClass.simpleName}")
        val container = bundle?.getSparseParcelableArray<Parcelable>("ss_container_${view.javaClass.simpleName}")
        if (container != null) {
            view.restoreHierarchyState(container)
        }
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