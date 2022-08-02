package com.tory.nestedceiling.widget;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.tory.nestedceiling.R;

/**
 * - Author: xutao
 * - Date: 2021/9/18
 * - Description:
 */
public class NestedCeilingHelper {
    /**
     * Log开关
     */
    public static boolean DEBUG = false;

    // 使用自带的overScroll来传递fling事件
    public static final boolean USE_OVER_SCROLL = true;

    /**
     * 将标记为
     * @param view
     */
    public static void setNestedChildContainerTag(@NonNull View view) {
        view.setTag(R.id.nested_child_item_container, Boolean.TRUE);
    }

    /**
     * 判断是否是包裹类
     * @param view
     * @return
     */
    public static boolean isNestedChildContainerTag(@NonNull View view) {
        return view.getTag(R.id.nested_child_item_container) == Boolean.TRUE
                || view instanceof NestedChildItemContainer;
    }

    /**
     * 重写高度
     * @param view
     * @param heightSpec
     * @return
     */
    public static int wrapContainerMeasureHeight(@NonNull View view, int heightSpec) {
        ViewParent parent = view.getParent();
        if (!(parent instanceof NestedParentRecyclerView)) {
            return heightSpec;
        }
        if (View.MeasureSpec.getMode(heightSpec) == View.MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException(view + " must be exactly height, layoutParam must be MATCH_PARENT");
        }

        NestedParentRecyclerView parentView = (NestedParentRecyclerView) parent;
        int parentHeight = View.MeasureSpec.getSize(heightSpec);
        int parentTopOffset = parentView.getTopOffset();
        final int newHeightSpec;
        if (parentHeight > parentTopOffset) {
            newHeightSpec = View.MeasureSpec.makeMeasureSpec(parentHeight - parentTopOffset, View.MeasureSpec.EXACTLY);
        } else {
            newHeightSpec = View.MeasureSpec.makeMeasureSpec(parentHeight, View.MeasureSpec.EXACTLY);
        }
        return newHeightSpec;
    }
}
