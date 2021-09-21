package com.tory.nestedceiling.app.page

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.adapter.item.*
import com.tory.nestedceiling.app.model.*
import com.tory.nestedceiling.widget.NestedParentRecyclerView
import com.tory.nestedceiling.widget.OnChildAttachStateListener
import com.drakeet.multitype.MultiTypeAdapter
import com.tory.module_adapter.base.NormalModuleAdapter
import com.tory.nestedceiling.app.views.*
import java.util.*

class NestedParentRecyclerViewActivity : AppCompatActivity() {


    private val isViewPager2: Boolean
        get() = intent?.getBooleanExtra("isViewPager2", false) == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nestedrecyclerview)

        val listAdapter = NormalModuleAdapter()
        listAdapter.register {
            ModuleBannerView(it.context)
        }
        listAdapter.register {
            ModuleNormalItemView(it.context)
        }
        listAdapter.register {
            LastViewPagerItemView(it.context)
        }
        listAdapter.register {
            LastViewPager2ItemView(it.context)
        }



        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val recyclerView = findViewById<NestedParentRecyclerView>(R.id.nested_rv)
        val topAnchor = findViewById<ImageView>(R.id.top_anchor)
        recyclerView.layoutManager = listAdapter.getGridLayoutManager(this)
        recyclerView.adapter = listAdapter

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }
        topAnchor.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }


        val data: MutableList<Any> = ArrayList()
        data.add(ModuleBannerModel())
        data.add(ModuleNormalItemModel(R.drawable.drawable_icons))
        data.add(ModuleNormalItemModel(R.drawable.drawable_new))
        data.add(ModuleNormalItemModel(R.drawable.drawable_recommend))
        if (isViewPager2) {
            data.add(LastViewPager2Model())
        } else {
            data.add(LastViewPagerModel())
        }
        listAdapter.setItems(data)

        recyclerView.addOnChildAttachStateListener(object :OnChildAttachStateListener{
            override fun onChildDetachedFromTop() {
                topAnchor.visibility = View.GONE
            }

            override fun onChildAttachedToTop() {
                topAnchor.visibility = View.VISIBLE
            }

        })
    }
}