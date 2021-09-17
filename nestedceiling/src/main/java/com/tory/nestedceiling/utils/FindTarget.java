package com.tory.nestedceiling.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tory.nestedceiling.widget.NestedChildRecyclerView;

public final class FindTarget {

    private static final int[] sTempLocation = new int[2];

    /**
     * 查找嵌套的子RecyclerView
     * @param sourceView
     * @return
     */
    @Nullable
    public static NestedChildRecyclerView findChildScrollTarget(@Nullable View sourceView) {
        if(sourceView == null || sourceView.getVisibility() != View.VISIBLE) {
            return null;
        }
        if (sourceView instanceof NestedChildRecyclerView) {
            return (NestedChildRecyclerView) sourceView;
        }
        if (!(sourceView instanceof ViewGroup)) {
            return null;
        }
        ViewGroup contentView = (ViewGroup) sourceView;
        int childCount = contentView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View view = contentView.getChildAt(i);
            int centerX = (view.getLeft() + view.getRight()) / 2;
            int contentLeft = contentView.getScrollX();
            if (centerX <= contentLeft || centerX >= contentLeft + contentView.getWidth()) {
                continue;
            }
            NestedChildRecyclerView target = findChildScrollTarget(view);
            if(target != null){
                return target;
            }
        }
        return null;
    }


    public static boolean isTouchPointInView(@Nullable View view, @Nullable MotionEvent event) {
        if (view == null || event == null) {
            return false;
        }
        view.getLocationOnScreen(sTempLocation);
        int left = sTempLocation[0];
        int top = sTempLocation[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        return x > left && x < right && y > top && y < bottom;
    }
}
