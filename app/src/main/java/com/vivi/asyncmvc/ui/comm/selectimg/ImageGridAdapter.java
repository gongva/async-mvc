package com.vivi.asyncmvc.ui.comm.selectimg;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Image;
import com.vivi.asyncmvc.library.plugs.glide.AImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Grid图片Adapter
 * Created by gongwei on 2019.1.3
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = false;
    private int mMaxSelect;//最大可选照片数
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    public List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;

    private CallBackMultiImageSelector callBack;

    public ImageGridAdapter(Context context, boolean showCamera, int maxSelect, CallBackMultiImageSelector callBack) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        this.mMaxSelect = maxSelect;
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);
        this.callBack = callBack;
    }

    /**
     * 显示选择指示器
     *
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b)
            return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     *
     * @param image
     */
    public void select(Image image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     *
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        mSelectedImages.clear();
        for (String path : resultList) {
            Image image = getImageByPath(path);
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        notifyDataSetChanged();
    }

    private Image getImageByPath(String path) {
        //照片要分相册显示，为了解决已选照片下标编号在每个相册中都是从1开始的问题，改为直接new一个Image
        return new Image(path, null, 0);
    }

    /**
     * 设置数据集
     *
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    public List<Image> getData() {
        return mImages;
    }

    /**
     * 重置每个Column的Size
     *
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if (mItemSize == columnWidth) {
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public Image getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        if (type == TYPE_CAMERA) {
            view = mInflater.inflate(R.layout.item_grid_image_camera, viewGroup, false);
        } else if (type == TYPE_NORMAL) {
            ViewHolder holde;
            if (view == null) {
                view = mInflater.inflate(R.layout.item_grid_image, viewGroup, false);
                holde = new ViewHolder(view);
            } else {
                holde = (ViewHolder) view.getTag();
                if (holde == null) {
                    view = mInflater.inflate(R.layout.item_grid_image, viewGroup, false);
                    holde = new ViewHolder(view);
                }
            }
            if (holde != null) {
                holde.bindData(getItem(i));
            }
            //add by gw
            holde.image.setTag(R.id.imgIndexTag, i);
            holde.image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int position = (int) v.getTag(R.id.imgIndexTag);
                        callBack.clickImage(position);
                    }
                    return true;
                }
            });
            holde.indicatorLayout.setTag(i);
            holde.indicatorLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int position = (int) v.getTag();
                        callBack.clickCheckBox(position);
                    }
                    return true;
                }
            });
        }
        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
        if (lp.height != mItemSize) {
            view.setLayoutParams(mItemLayoutParams);
        }
        return view;
    }

    class ViewHolder {
        ImageView image;
        TextView indicator;
        LinearLayout indicatorLayout;

        ViewHolder(View view) {
            image = view.findViewById(R.id.image);
            indicator = view.findViewById(R.id.checkmark);
            indicatorLayout = view.findViewById(R.id.llt_checkmark);
            view.setTag(this);
        }

        void bindData(final Image data) {
            if (data == null)
                return;
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) {
                    // 设置选中状态
                    setCheck(indicator, mSelectedImages.indexOf(data));
                } else {
                    // 未选择
                    setUnCheck(indicator);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }

            // 显示图片
            //AImage.load(data.path, image, mItemSize, mItemSize);
            File imageFile = new File(data.path);
            try {
                AImage.loadCenterCropResizeFile(imageFile, image, mItemSize, mItemSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setCheck(TextView check, int index) {
        check.setBackgroundResource(mMaxSelect > 1 ? R.drawable.shap_multi_image_selected : R.drawable.ic_img_check_off);
        check.setText(mMaxSelect > 1 ? String.valueOf(index + 1) : "");
    }

    private void setUnCheck(TextView check) {
        check.setBackgroundResource(R.drawable.ic_img_check_off);
        check.setText("");
    }

    public interface CallBackMultiImageSelector {
        void clickImage(int position);

        void clickCheckBox(int position);
    }
}
