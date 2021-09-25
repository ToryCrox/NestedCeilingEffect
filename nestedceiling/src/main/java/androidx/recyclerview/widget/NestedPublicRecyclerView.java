package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * - Author: xutao
 * - Date: 2021/9/16
 * - Email: xutao@shizhuang-inc.com
 * - Description:
 */
public class NestedPublicRecyclerView extends RecyclerView {


    public NestedPublicRecyclerView(@NonNull Context context) {
        super(context);
    }

    public NestedPublicRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedPublicRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void scrollStep(int dx, int dy, @Nullable int[] consumed) {
        super.scrollStep(dx, dy, consumed);
    }

    public void scrollConsumed(int dx, int dy, @Nullable int[] consumed) {
        scrollStep(dx, dy, consumed);
    }

    @Nullable
    public OverScroller getFlingOverScroll() {
        return mViewFlinger.mOverScroller;
    }

    /**
     * 是否使用自带的Fling
     * @return
     */
    public boolean enableOverScrollFling() {
        return getFlingOverScroll() != null;
    }

    /**
     * 更新滚动状态
     * @param scrollState
     */
    public void updateScrollState(int scrollState) {
        setScrollState(scrollState);
    }
}
