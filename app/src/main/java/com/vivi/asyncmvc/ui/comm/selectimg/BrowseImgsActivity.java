package com.vivi.asyncmvc.ui.comm.selectimg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageCompleteEvent;
import com.vivi.asyncmvc.comm.event.selectimg.SelectImageDoingEvent;
import com.vivi.asyncmvc.comm.listener.OnClickExListener;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 三种情况,选择图片，管理已选择的图片，查看图片
 * ImgType{SELECT_IMG, SELECTED_IMG, SHOW_IMG}
 * Created by Mark-Office on 2016/4/29.
 */
public class BrowseImgsActivity extends BaseActivity {

    private static final float Y_OFFSET_TOP_TOAST = 30f;

    public static final String JUMP_RESULT = "jump_result";

    @BindView(R.id.vp_browse_img)
    ViewPagerFixed mViewpager;
    @BindView(R.id.tv_select_data)
    TextView tvSelectData;
    @BindView(R.id.btn_commit)
    TextView btnCommit;
    @BindView(R.id.ryt_bottom)
    RelativeLayout rytBottom;
    @BindView(R.id.rlt_browse_imgs_nav)
    RelativeLayout rltNav;
    @BindView(R.id.tv_browse_imgs_back)
    TextView tvNavBack;
    @BindView(R.id.tv_browse_imgs_title)
    TextView tvNavTitle;
    @BindView(R.id.llt_browse_imgs_menu)
    LinearLayout lltNavMenu;
    @BindView(R.id.tv_browse_imgs_menu)
    TextView tvNavMenu;

    //全部图片
    private ArrayList<String> mPicList = new ArrayList<>();
    //当前图片
    private int mPosition;
    //选择类型
    private ImgType mImgType;
    //已选择的图片列表
    private ArrayList<String> mSelectList = new ArrayList<>();
    //选择图片最大数
    private int maxSelect;
    //当前位置
    private int currentPosition;
    //是否横屏显示
    private boolean isLandscape;

    private ImagePagerAdapter adapter;

    private boolean boolDelEd = false;//是否有删除照片的动作，若有，则回到列表时需刷新

    private Toast mToast;//用于SHOW_IMG时，显示当前页/总页数所用

    private TextView tvMenu;

    //SELECT_IMG,图片库中选择图片
    //SELECTED_IMG,选择好的图片
    //SHOW_IMG,查看图片
    public enum ImgType {
        SELECT_IMG,
        SELECTED_IMG,
        SHOW_IMG
    }

    /**
     * 横屏浏览图片列表
     * @param context
     * @param picList
     * @param position
     */
    public static void startForShowImgLan(Context context, ArrayList<String> picList, int position) {
        startForShowImg(context, picList, position, true);
    }

    /**
     * 浏览单张图片
     * @param context
     * @param pic
     */
    public static void startForShowImg(Context context, String pic) {
        ArrayList<String> picList = new ArrayList();
        picList.add(pic);
        startForShowImg(context, picList, 0);
    }

    /**
     * 浏览多张图片
     * @param context
     * @param picList
     * @param position
     */
    public static void startForShowImg(Context context, ArrayList<String> picList, int position) {
        startForShowImg(context, picList, position, false);
    }

    /**
     * 浏览大图
     *
     * @param context
     * @param picList     所有图片
     * @param position    选中图片的位置
     * @param isLandscape 是否横屏显示
     */
    private static void startForShowImg(Context context, ArrayList<String> picList, int position, boolean isLandscape) {
        Intent intent = new Intent(context, BrowseImgsActivity.class);
        intent.putExtra("imgType", ImgType.SHOW_IMG);
        intent.putStringArrayListExtra("picList", picList);
        intent.putExtra("position", position);
        intent.putExtra("isLandscape", isLandscape);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.browse_img_open, R.anim.browse_img_open_for_out);
    }

    /**
     * 从相册浏览过来选图
     *
     * @param context
     * @param picList    所有图片
     * @param position   选中图片的位置
     * @param selectList 选择好的图片
     * @param maxSelect  最大选择数
     */
    public static void startForSelect(Activity context, ArrayList<String> picList, int position,
                                      ArrayList<String> selectList, int maxSelect) {
        Intent intent = new Intent(context, BrowseImgsActivity.class);
        intent.putExtra("imgType", ImgType.SELECT_IMG);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("picList", picList);
        intent.putStringArrayListExtra("selectList", selectList);
        intent.putExtra("maxSelectCount", maxSelect);
        context.startActivity(intent);
    }

    /**
     * 已选的大图编辑（右上角带删除）
     *
     * @param context
     * @param picList
     * @param position
     */
    public static void startForSelected(Activity context, ArrayList<String> picList, int position) {
        Intent intent = new Intent(context, BrowseImgsActivity.class);
        intent.putExtra("imgType", ImgType.SELECTED_IMG);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("picList", picList);
        context.startActivityForResult(intent, AppContext.REQUEST_CODE_SELECTED_IMAGES_EDIT);
    }

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_browse_imgs;
    }

    @Override
    protected void initIntentData(Intent intent) {
        super.initIntentData(intent);
        mPicList = getIntent().getStringArrayListExtra("picList");
        currentPosition = mPosition = getIntent().getIntExtra("position", 0);
        mImgType = (ImgType) getIntent().getSerializableExtra("imgType");
        isLandscape = getIntent().getBooleanExtra("isLandscape", false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarTransparent(true);
        super.onCreate(savedInstanceState);
        hideActionBar();

        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        BusProvider.bindLifecycle(this);

        if (mPicList == null) {
            mPicList = new ArrayList<>();
        }

        //setTitle((mPosition + 1) + "/" + mPicList.size());
        setNavTitle((mPosition + 1) + "/" + mPicList.size());
        if (mImgType == ImgType.SELECT_IMG) {
            mSelectList = getIntent().getStringArrayListExtra("selectList");
            maxSelect = getIntent().getIntExtra("maxSelectCount", 0);
            rltNav.setVisibility(View.VISIBLE);
            setStatusBarTransparent(false);
            rytBottom.setVisibility(View.VISIBLE);

            if (mSelectList == null) {
                mSelectList = new ArrayList<>();
            }

            refreshSelectedNum();

            int index = getCurrentIndex(mPicList.get(currentPosition));
            View menu = View.inflate(this, R.layout.view_browse_image_count, null);
            tvMenu = ((TextView) menu.findViewById(R.id.tv_view_browse_image_count));
            tvMenu.setText((index >= 0 && isMulti()) ? String.valueOf(index + 1) : "");
            tvMenu.setBackgroundResource((index >= 0 ? (isMulti() ? R.drawable.shape_oval_yellow : R.drawable.ic_img_check_on) : R.drawable.ic_img_check_off));
            lltNavMenu.removeAllViews();
            lltNavMenu.addView(menu, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCurrentIndex(mPicList.get(currentPosition)) >= 0) {
                        mSelectList.remove(mPicList.get(currentPosition));
                        setUnCheck();
                        refreshSelectedNum();
                    } else {
                        if (mSelectList.size() < maxSelect) {
                            mSelectList.add(mPicList.get(currentPosition));
                            setCheck(getCurrentIndex(mPicList.get(currentPosition)));
                            refreshSelectedNum();
                        } else {
                            if (isMulti()) {
                                UI.showMultiSelectLimitDialog(BrowseImgsActivity.this, maxSelect);
                            } else {
                                //若上限为1张，选择后一张时把前面选的取消掉，再选中当前图片
                                mSelectList.clear();
                                mSelectList.add(mPicList.get(currentPosition));
                                setCheck(getCurrentIndex(mPicList.get(currentPosition)));
                                refreshSelectedNum();
                            }
                        }
                    }
                }
            });
        } else if (mImgType == ImgType.SELECTED_IMG) {
            rltNav.setVisibility(View.VISIBLE);
            setStatusBarTransparent(false);
            tvNavMenu.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_trash, 0);
            tvNavMenu.setOnClickListener(new OnClickExListener() {
                @Override
                public void onClickEx(View v) {
                    //modify by gw
                    boolDelEd = true;
                    if (mPicList.size() > (currentPosition + 1)) {
                        //删的不是最后一张
                        mPicList.remove(currentPosition);
                        setPagerAdapter();
                        setNavTitle((currentPosition + 1) + "/" + mPicList.size());
                    } else if (currentPosition > 0) {
                        //删的是最后一张，但当前不止一张
                        mPicList.remove(currentPosition);
                        currentPosition--;
                        setPagerAdapter();
                        setNavTitle((currentPosition + 1) + "/" + mPicList.size());
                    } else {
                        mPicList.remove(currentPosition);
                        finishSelectedImg();
                        setPagerAdapter();
                    }
                }
            }.setCustomerClickDelayTime(300));
        } else if (mImgType == ImgType.SHOW_IMG) {
            //gw: 查看图片时直接全屏，自己写的nav也不要了
            rltNav.setVisibility(View.GONE);
            setStatusBarTransparent(true);
            lltNavMenu.setVisibility(View.GONE);
            showPageIndexToast();
        }

        setPagerAdapter();
        addListener();
    }

    protected void setStatusBarTransparent(boolean trans) {
        if (trans) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //super.setStatusBarTransparent(true);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //super.setStatusBarTransparent(false);
        }
    }

    private void setNavTitle(String title) {
        tvNavTitle.setText(title);
    }

    /*
     * 是否为多选
     * 单选时只需要钩钩，多选时才需要数字
     * */
    private boolean isMulti() {
        return maxSelect > 1;
    }

    /**
     * add by gw
     */
    private void refreshSelectedNum() {
        if (mSelectList.size() > 0) {
            btnCommit.setEnabled(true);
            btnCommit.setTextColor(getResources().getColor(R.color.white));
            if (maxSelect == 1) {
                tvSelectData.setVisibility(View.GONE);
            } else {
                tvSelectData.setVisibility(View.VISIBLE);
                tvSelectData.setText(String.valueOf(mSelectList.size()));
            }
        } else {
            btnCommit.setEnabled(false);
            btnCommit.setTextColor(getResources().getColor(R.color.btn_disable
            ));
            tvSelectData.setVisibility(View.GONE);
        }
    }

    /**
     * add by gw
     */
    private void setPagerAdapter() {
        adapter = new ImagePagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(adapter);// 设置适配器
        mViewpager.setOffscreenPageLimit(/*adapter.getCount() > 3 ? 3 : adapter.getCount() - 1*/3);
        mViewpager.setCurrentItem(currentPosition, false);
        adapter.notifyDataSetChanged();
    }

    /**
     * add by gw
     */
    private void finishSelectedImg() {
        if (mImgType == ImgType.SELECTED_IMG) {
            //已选照片进来时，一旦有删除动作，则返回时需更新数据
            if (boolDelEd) {
                Intent intent = getIntent();
                intent.putStringArrayListExtra(JUMP_RESULT, mPicList);
                setResult(RESULT_OK, intent);
            }
            finish();
        } else if (mImgType == ImgType.SELECT_IMG) {
            //从相册选择照片过来，返回时需把已选照片带回去
            BusProvider.post(new SelectImageDoingEvent(mSelectList));
            finish();
        } else {
            //查看进来，直接返回
            closePageForShowImg();
        }
    }

    /**
     * 封装一个从ResultOK的Intent中获取编辑后的图片列表的方法
     * startActivityForResult过来的Activity使用
     * @param data
     * @return
     */
    public static ArrayList getImagesFromResultOk(Intent data) {
        if (data != null) {
            return data.getStringArrayListExtra(JUMP_RESULT);
        }
        return null;
    }

    public void closePageForShowImg() {
        finish();
        overridePendingTransition(R.anim.browse_img_close_for_in, R.anim.browse_img_close);
    }

    private void closePageForCommit() {
        finish();
        overridePendingTransition(R.anim.browse_img_close_for_in, R.anim.translate_out_from_bottom);
    }

    /* 标题栏+底部操作栏，显示则隐藏，隐藏则显示 */
    private void switchNavAndBottomBar() {
        if (rltNav.getVisibility() == View.VISIBLE) {
            rltNav.setVisibility(View.GONE);
            setStatusBarTransparent(true);
            if (mImgType == ImgType.SELECT_IMG) {
                rytBottom.setVisibility(View.GONE);
            }
        } else {
            rltNav.setVisibility(View.VISIBLE);
            setStatusBarTransparent(false);
            if (mImgType == ImgType.SELECT_IMG) {
                rytBottom.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addListener() {
        mViewpager.setOnPageChangeListener(pageChangeListener);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_commit:
                        if (mImgType == ImgType.SELECT_IMG && mSelectList.size() > 0) {
                            BusProvider.post(new SelectImageCompleteEvent(mSelectList));
                            closePageForCommit();
                        }
                        break;
                }
            }
        };
        btnCommit.setOnClickListener(onClickListener);
        tvNavBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishSelectedImg();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishSelectedImg();
    }

    /**
     * 当前图片在已选列表中的下标，-1表示不在其中
     *
     * @param s
     */
    private int getCurrentIndex(String s) {
        int size = mSelectList.size();
        for (int i = 0; i < size; i++) {
            if (s != null && s.equals(mSelectList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /*
     * 设置右上角菜单选中，含钩钩选中和数字选中
     * */
    private void setCheck(int index) {
        tvMenu.setBackgroundResource(isMulti() ? R.drawable.shape_oval_yellow : R.drawable.ic_img_check_on);
        tvMenu.setTextColor(ColorStateList.valueOf(0xFFFF6284));
        tvMenu.setText(isMulti() ? String.valueOf(index + 1) : "");
    }

    /*
     * 设置右上角菜单未选中
     * */
    private void setUnCheck() {
        tvMenu.setBackgroundResource(R.drawable.ic_img_check_off);
        tvMenu.setText("");
    }

    private void showPageIndexToast() {
        if (mPicList.size() <= 1) {
            //gw: 哎呀,1/1这种，就不显示了
            return;
        }
        if (mToast == null) {
            mToast = new Toast(this);
            mToast.setDuration(Toast.LENGTH_SHORT);
            TextView tv = new TextView(this);
            tv.setBackgroundResource(R.color.color_transparent);
            tv.setTextColor(ColorStateList.valueOf(0xffeaeaea));
            tv.setTextSize(15);
            mToast.setView(tv);
            mToast.setGravity(Gravity.TOP, 0, ViewUtils.dip2px(this, Y_OFFSET_TOP_TOAST));
        }
        ((TextView) mToast.getView()).setText(String.format("%s/%s", currentPosition + 1, mPicList.size()));
        mToast.show();
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        public void onPageSelected(int arg0) {// 页面选择响应函数
            currentPosition = arg0;
            String pageIndexAndTotalPage = String.format("%s/%s", currentPosition + 1, mPicList.size());
            //setTitle(pageIndexAndTotalPage);
            setNavTitle(pageIndexAndTotalPage);
            if (mImgType == ImgType.SELECT_IMG) {
                int index = getCurrentIndex(mPicList.get(currentPosition));
                if (index >= 0) {
                    setCheck(index);
                } else {
                    setUnCheck();
                }
            } else if (mImgType == ImgType.SHOW_IMG) {
                showPageIndexToast();
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {// 滑动中。。。
        }

        public void onPageScrollStateChanged(int arg0) {// 滑动状态改变

        }
    };

    class ImagePagerAdapter extends FragmentStatePagerAdapter {


        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mPicList == null ? 0 : mPicList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String uri = mPicList.get(position);
            if (CommonTools.isServerImageUrl(uri)) {
                uri = AppContext.BASE_URL_IMAGE + uri;
            }
            ImageDetailFragment f = ImageDetailFragment.newInstance(uri, R.drawable.ic_img_default);
            f.setCallBack(new ImageDetailFragment.CallBackImageDetailFragment() {
                @Override
                public void imageClick() {
                    if (mImgType == ImgType.SHOW_IMG) {
                        //单击关页面
                        closePageForShowImg();
                    } else {
                        //单击隐藏导航栏
                        switchNavAndBottomBar();
                    }
                }

                @Override
                public void imageLongClick() {

                }
            });
            return f;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
