package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tory.nestedceiling.widget.NestedCeilingHelper;

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

    public void doScrollConsumed(int dx, int dy, @NonNull int[] consumed) {
        consumed[0] = 0;
        consumed[1] = 1;
        scrollStep(dx, dy, consumed);
        int consumedX = consumed[0];
        int consumedY = consumed[1];
        if (consumedX != 0 || consumedY != 0) {
            // 分发滚动状态
            dispatchOnScrolled(consumedX, consumedY);
        }
    }

    @Nullable
    public OverScroller getFlingOverScroll() {
        return mViewFlinger.mOverScroller;
    }

    /**
     * 是否正在Fling
     * @return
     */
    public boolean isFling() {
        OverScroller overScroller = getFlingOverScroll();
        return overScroller != null && !overScroller.isFinished();
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

    /**
     * 停止滚动，但不更新状态
     */
    public void stopScrollWithoutState() {
        mViewFlinger.stop();
        LayoutManager layout = getLayoutManager();
        if (layout != null) {
            layout.stopSmoothScroller();
        }
    }

    /**
     * Fling到边缘时回调
     * @param velocityX
     * @param velocityY
     */
    @Override
    void absorbGlows(int velocityX, int velocityY) {
        //super.absorbGlows(velocityX, velocityY);
        if (NestedCeilingHelper.DEBUG) {
            Log.d("NestedPublicRecycler", "absorbGlows velocityY:" + velocityY);
        }
        onFlingEnd(velocityX, velocityY);
    }

    /**
     * Fling到边缘时回调
     * @param velocityX
     * @param velocityY
     */
    protected void onFlingEnd(int velocityX, int velocityY) {

    }
}
