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
import java.util.*

class NestedParentRecyclerViewActivity : AppCompatActivity() {


    private val isViewPager2: Boolean
        get() = intent?.getBooleanExtra("isViewPager2", false) == true

    private val data: List<Any>
        get() {
            val data: MutableList<Any> = ArrayList()
            data.add(BannerData())
            data.add(R.drawable.drawable_icons)
            data.add(R.drawable.drawable_new)
            data.add(R.drawable.drawable_recommend)
            if (isViewPager2) {
                data.add(LastViewPager2())
            } else {
                data.add(LastViewPager())
            }
            return data
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nestedrecyclerview)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        val recyclerView = findViewById<NestedParentRecyclerView>(R.id.nested_rv)
        val topAnchor = findViewById<ImageView>(R.id.top_anchor)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MultiTypeAdapter(data)
        adapter.register(BannerItem())
        adapter.register(NormalItem())
        adapter.register(LastViewPager2Item(recyclerView))
        adapter.register(LastViewPagerItem(recyclerView))
        recyclerView.adapter = adapter

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }
        topAnchor.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

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