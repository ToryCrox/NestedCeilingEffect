package com.tory.nestedceiling.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.NestedPublicRecyclerView;

/**
 * 嵌套滑动子View，一定要继承该View
 */
public class NestedChildRecyclerView extends NestedPublicRecyclerView implements NestedChildItemContainer {

    public NestedChildRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setNestedScrollingEnabled(true);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }
}
