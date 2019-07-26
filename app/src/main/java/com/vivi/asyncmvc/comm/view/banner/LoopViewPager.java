package com.vivi.asyncmvc.comm.view.banner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自动滚动以及无限循环的ViewPager
 *
 * @author gongwei
 * @Date 2019.1.10
 */
public class LoopViewPager extends ViewPager implements Runnable {

    private static final int POST_DELAYED_TIME = 5 * 1000;
    private PagerAdapter pagerAdapter;
    private PagerAdapterWrapper pagerAdapterWrapper;
    private int duration = POST_DELAYED_TIME;
    private GestureDetector mGestureDetector;

    // 是否可以滑动
    private boolean isCanScroll = true;
    //是否自动混动下一页，默认滚动
    private boolean isAutoNext = true;

    public LoopViewPager(Context context) {
        this(context, null);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(new YScrollDetector());
    }

    /**
     * 是否可以滑动，默认可以左右滑动
     *
     * @param isCanScroll true可以滑动 | false不能滑动
     */
    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public void startAutoCycle() {
        startAutoCycle(duration);
    }

    public void setAutoCycle(boolean autoCycle) {
        isAutoNext = autoCycle;
    }

    public void startAutoCycle(int duration) {
        isAutoNext = true;
        this.duration = duration >= 1000 ? duration : 1000;
        removeCallbacks(this);
        postDelayed(this, duration);
    }

    public void stopAutoCycle() {
        removeCallbacks(this);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (pagerAdapterWrapper.isCycleplay()) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoCycle();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (isAutoNext) {
                        startAutoCycle();
                    }
                    break;
            }
        }
        // 这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
        getParent().requestDisallowInterceptTouchEvent(!mGestureDetector.onTouchEvent(ev));
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    /**
     * 这里只有继承了BannerPagerAdapter类，notifyDataSetChanged通知才有效
     */
    //@Override
    public void setAdapter(BannerPagerAdapter adapter) {
        if (adapter != null) {
            pagerAdapter = adapter;
            pagerAdapterWrapper = new PagerAdapterWrapper(adapter);
            super.setAdapter(pagerAdapterWrapper);
            if (adapter.getCount() != 0) {
                setCurrentItem(0, false);
            }
        }
    }

    @Override
    public PagerAdapter getAdapter() {
        return pagerAdapter;
    }

    @Override
    public int getCurrentItem() {
        if (pagerAdapterWrapper.isCycleplay()) {
            return super.getCurrentItem() - 1;
        }

        return super.getCurrentItem();
    }

    private int getSuperCurrentItem() {
        return super.getCurrentItem();
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    public void nextItem() {
        setCurrentItem(getCurrentItem() + 1);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (pagerAdapterWrapper.isCycleplay()) {
            super.setCurrentItem(item + 1, smoothScroll);
            return;
        }

        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        addOnPageChangeListener(listener);
    }

    @Override
    public void addOnPageChangeListener(final OnPageChangeListener listener) {
        //super.addOnPageChangeListener(listener);
        super.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                listener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                listener.onPageSelected(getRealPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                listener.onPageScrollStateChanged(state);
            }
        });
    }

    // 自动滚动关键
    @Override
    public void run() {
        if (pagerAdapterWrapper.isCycleplay()) {
            if (pagerAdapter != null && pagerAdapter.getCount() > 1) {
                nextItem();
            }
            startAutoCycle(duration);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAutoCycle();
    }

    /**
     * 获取实际的position
     *
     * @param position
     * @return
     */
    public int getRealPosition(int position) {
        if (pagerAdapterWrapper.isCycleplay()) {
            int count = pagerAdapter.getCount();
            return position == 0 ? count - 1 : (position == count + 1 ? 0 : position - 1);
        }

        return position;
    }

    // 对setAdapter的数据进行包装
    private class PagerAdapterWrapper extends PagerAdapter {
        private BannerPagerAdapter adapter;

        public PagerAdapterWrapper(BannerPagerAdapter adapter) {
            adapter.setAdapterWrapper(this);
            this.adapter = adapter;
        }

        // 关键之一:修改Count长度
        @Override
        public int getCount() {
            return isCycleplay() ? adapter.getCount() + 2 : adapter.getCount();
        }

        public boolean isCycleplay() {
            return adapter.canCyclePlay() && adapter.getCount() > 1;
        }

        // 这里是关键之二:修改索引(如果不考虑内容问题可以全部加载进数组然后操作更简单)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return adapter.instantiateItem(container, getRealPosition(position));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            adapter.destroyItem(container, getRealPosition(position), object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
            if (isCycleplay()) {
                int item = getRealPosition(getSuperCurrentItem());
                setCurrentItem(item, false);
                adapter.finishUpdate(container);
            }
        }

        @Override
        public boolean isViewFromObject(View paramView, Object paramObject) {
            return adapter.isViewFromObject(paramView, paramObject);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    static public abstract class BannerPagerAdapter extends PagerAdapter {

        private PagerAdapter adapterWrapper;

        public void setAdapterWrapper(PagerAdapter adapterWrapper) {
            this.adapterWrapper = adapterWrapper;
        }

        @Override
        public void notifyDataSetChanged() {
            if (adapterWrapper != null) {
                adapterWrapper.notifyDataSetChanged();
            }
        }

        @Override
        public boolean isViewFromObject(View paramView, Object paramObject) {
            return paramView == paramObject; // 官方提示这样写
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        /**
         * 是否允许循环滚动
         * 默认允许，具体业务若不允许，请Override并return false
         *
         * @return
         */
        public boolean canCyclePlay() {
            return true;
        }

        public abstract void clear();

        public abstract void setData(Object... objects);
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {
                return true;
            }
            return false;
        }
    }
}
