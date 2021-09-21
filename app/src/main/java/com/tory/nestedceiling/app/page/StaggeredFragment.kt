package com.tory.nestedceiling.app.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.tory.module_adapter.base.NormalModuleAdapter
import com.tory.nestedceiling.app.views.*
import com.tory.nestedceiling.widget.NestedChildRecyclerView

class StaggeredFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = NestedChildRecyclerView(requireContext())

        val listAdapter = NormalModuleAdapter()
        listAdapter.register (gridSize = 2) {
            ModuleStaggerItemView(it.context)
        }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)

        listAdapter.setItems(MutableList(200) {
            ModuleStaggerItemModel()
        })

        return recyclerView
    }



    fun resetToTop() {

    }
}