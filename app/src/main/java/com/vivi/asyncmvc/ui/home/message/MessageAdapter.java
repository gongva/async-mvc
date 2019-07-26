package com.vivi.asyncmvc.ui.home.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.studio.util.DateUtils;
import android.studio.view.widget.SimpleAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Message;
import com.vivi.asyncmvc.api.entity.message.DismissAlVehicleExpand;
import com.vivi.asyncmvc.api.entity.message.DlExaminedExpand;
import com.vivi.asyncmvc.api.entity.message.FnExpand;
import com.vivi.asyncmvc.api.entity.message.IllegalExpand;
import com.vivi.asyncmvc.api.entity.message.MovingCarExpand;
import com.vivi.asyncmvc.api.entity.message.VehicleInspectionExpand;
import com.vivi.asyncmvc.api.entity.message.VlLockExpand;
import com.vivi.asyncmvc.comm.view.dialog.ListDialog;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.DataFormat;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.home.clicense.CLicenseBindModeActivity;
import com.vivi.asyncmvc.ui.home.dlicense.DLicenseBindCheckActivity;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息中心适配器
 *
 * @author gongwei
 * @date 2019/2/14
 */
public class MessageAdapter extends SimpleAdapter<Message> {

    //resources
    protected Map<Integer, Integer> resLayout = new LinkedHashMap<>();
    //tools
    protected Activity mContext;
    protected LayoutInflater mInflater;
    protected MessageOnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(MessageOnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public MessageAdapter(Activity context) {
        super(context);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        initItemLayout();
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup, int i) {
        View view = createView(i);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    protected View createView(int i) {
        Message item = getItem(i);
        Integer layoutResId = resLayout.get(item.msgType);
        if (layoutResId == null) {//增加容错率，默认为：备案机动车提醒
            layoutResId = resLayout.get(Message.APPLY_EDL);
        }
        return mInflater.inflate(layoutResId, null);
    }

    @Override
    public int getViewTypeCount() {
        return resLayout.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message item = getItem(position);
        int i = getKeyIndex(item.msgType);
        if (i >= 0) {
            return i;
        }
        return getKeyIndex(Message.APPLY_EDL);
    }

    private int getKeyIndex(int msgType) {
        Integer[] keys = resLayout.keySet().toArray(new Integer[0]);
        for (int i = 0; i < resLayout.size(); i++) {
            if (msgType == keys[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取最后一条数据的创建时间
     * 列表为空时返回0
     *
     * @return
     */
    public long getLastMessageGmtCreate() {
        List<Message> data = getData();
        if (data == null || data.size() == 0) {
            return 0;
        } else {
            return data.get(data.size() - 1).gmtCreate;
        }
    }

    private void initItemLayout() {
        resLayout.put(Message.APPLY_EDL, R.layout.item_message_apply_edl);
        resLayout.put(Message.FILING_VEHICLE, R.layout.item_message_filing_vehicle);
        resLayout.put(Message.DISMISS_AL_VEHICLE, R.layout.item_message_dismiss_al_vehicle);
        resLayout.put(Message.FN, R.layout.item_message_fn);
        resLayout.put(Message.MOVING_CAR, R.layout.item_message_move_car);
        resLayout.put(Message.ILLEGAL, R.layout.item_message_illegal);
        resLayout.put(Message.DL_LOCK, R.layout.item_message_dl_lock);
        resLayout.put(Message.VL_LOCK, R.layout.item_message_vl_lock);
        resLayout.put(Message.DL_EXAMINED, R.layout.item_message_dl_examined);
        resLayout.put(Message.VEHICLE_INSPECTION, R.layout.item_message_vehicle_inspection);
    }

    @Override
    public void bindView(Context context, View view, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        final Message item = getItem(position);

        //处理所有类型的消息共有的信息
        holder.title.setText(item.msgTitle);
        holder.msgTime.setText(DataFormat.formatDate(item.gmtCreate));
        holder.redDot.setVisibility(item.read ? View.GONE : View.VISIBLE);
        //处理Long Click
        if (mOnItemLongClickListener != null) {
            holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(item);
                    return true;
                }
            });
        }
        //处理扩展信息和Click Listener
        switch (item.msgType) {
            case Message.APPLY_EDL:
                handleApplyEdl(holder, item);
                break;
            case Message.FILING_VEHICLE:
                handleFilingVehicle(holder, item);
                break;
            case Message.DISMISS_AL_VEHICLE:
                handleDismissAlVehicle(holder, item);
                break;
            case Message.FN:
                handleFn(holder, item);
                break;
            case Message.MOVING_CAR:
                handleMovingCar(holder, item);
                break;
            case Message.ILLEGAL:
                handleIllegal(holder, item);
                break;
            case Message.DL_LOCK:
                handleDlLock(holder, item);
                break;
            case Message.VL_LOCK:
                handleVlLock(holder, item);
                break;
            case Message.DL_EXAMINED:
                handleElExamined(holder, item);
                break;
            case Message.VEHICLE_INSPECTION:
                handleVehicleInspection(holder, item);
                break;
        }
    }

    /**
     * 设置已读
     *
     * @param message
     */
    private void setRead(final Message message) {
        if (!message.read) {
            Api.messageRead(message.id, null);
            message.read = true;
            notifyDataSetChanged();
            Message.updateAsRead(message.id);
        }
    }

    /**
     * 申领电子驾照
     *
     * @param holder
     * @param item
     */
    private void handleApplyEdl(ViewHolder holder, final Message item) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                DLicenseBindCheckActivity.start(mContext);
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
    }

    /**
     * 机动车备案提醒
     *
     * @param holder
     */
    private void handleFilingVehicle(ViewHolder holder, final Message item) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                CLicenseBindModeActivity.start(mContext);
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
    }

    /**
     * 机动车解除备案提醒
     *
     * @param holder
     * @param item
     */
    private void handleDismissAlVehicle(ViewHolder holder, final Message item) {
        final DismissAlVehicleExpand expand = DismissAlVehicleExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                MessageDetailActivity.start(mContext, item.msgTitle, item.gmtCreate, expand.description);
            }
        });
    }

    /**
     * 限号提醒
     *
     * @param holder
     * @param item
     */
    private void handleFn(ViewHolder holder, final Message item) {
        final FnExpand expand = FnExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        holder.subContent1.setText(expand.content);
        holder.businessTime.setText(expand.limitTimeRange);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                MessageDetailActivity.start(mContext, item.msgTitle, item.gmtCreate, expand.description);
            }
        });
    }

    /**
     * 挪车通知
     *
     * @param holder
     * @param item
     */
    private void handleMovingCar(ViewHolder holder, final Message item) {
        final MovingCarExpand expand = MovingCarExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        holder.subContent1.setText(expand.addr);
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                //todo
                UI.showToast("跳转一键挪车详情，id：" + expand.id);
            }
        });
    }

    /**
     * 违法行为
     *
     * @param holder
     * @param item
     */
    private void handleIllegal(ViewHolder holder, final Message item) {
        final IllegalExpand expand = IllegalExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        String illegalDatetime = DateUtils.formatDateTime(new Date(expand.illegalTime));
        holder.subContent1.setText(String.format("%s %s\n%s", illegalDatetime, expand.illegalAddr, expand.illegalContent));
        holder.subContent2.setText(String.format("罚款%s元 记分%s分", DataFormat.centToYuan(expand.fines), expand.illegalScores));
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                //todo
                UI.showToast("跳转违法详情，id：" + expand.id);
            }
        });
    }

    /**
     * 驾驶证锁定
     *
     * @param holder
     * @param item
     */
    private void handleDlLock(ViewHolder holder, final Message item) {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                //todo
                UI.showToast("跳转选择交管局并导航");
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
    }

    /**
     * 行驶证锁定
     *
     * @param holder
     * @param item
     */
    private void handleVlLock(ViewHolder holder, final Message item) {
        final VlLockExpand expand = VlLockExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                //todo
                UI.showToast("跳转选择交管局并导航");
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
    }

    /**
     * 驾驶证年审提醒
     *
     * @param holder
     * @param item
     */
    private void handleElExamined(ViewHolder holder, final Message item) {
        final DlExaminedExpand expand = DlExaminedExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(String.format("截止日期 %s", DataFormat.formatDate(expand.deadline, "MM月dd日")));
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                switch (v.getId()) {
                    case R.id.llt_message_item_root:
                        //todo
                        UI.showListDialog(mContext, new String[]{"去医院体检", "去车管所办理年审"}, "请选择", new ListDialog.ListDialogCallback() {
                            @Override
                            public void choose(int which) {
                                switch (which) {
                                    case 0:
                                        //todo
                                        UI.showToast("跳转选择医院");
                                        break;
                                    case 1:
                                        //todo
                                        UI.showToast("跳转选择车管所");
                                        break;
                                }
                            }
                        });
                        break;
                    case R.id.tv_message_action1:
                        //todo
                        UI.showToast("跳转选择医院");
                        break;
                    case R.id.tv_message_action2:
                        //todo
                        UI.showToast("跳转选择车管所");
                        break;
                }
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
        holder.action2.setOnClickListener(clickListener);
    }

    /**
     * 机动车年检提醒
     *
     * @param holder
     * @param item
     */
    private void handleVehicleInspection(ViewHolder holder, final Message item) {
        final VehicleInspectionExpand expand = VehicleInspectionExpand.getExpand(item.msgExt);
        if (expand == null) {
            return;
        }
        holder.content.setText(expand.licensePlateNumber);
        holder.subContent1.setText(String.format("截止日期 %s", DataFormat.formatDate(expand.deadline, "MM月dd日")));
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRead(item);
                //todo
                UI.showToast("跳转选择交管局并导航");
            }
        };
        holder.rootView.setOnClickListener(clickListener);
        holder.action1.setOnClickListener(clickListener);
    }

    private class ViewHolder {

        public View rootView;
        public ImageView icon;
        public View redDot;
        public TextView title;
        public TextView msgTime;//消息的创建时间
        public TextView content;
        public TextView subContent1;
        public TextView subContent2;
        public TextView businessTime;//某些业务里有时间
        public TextView action1;
        public TextView action2;//部分业务有2个操作项


        public ViewHolder(View view) {
            rootView = view.findViewById(R.id.llt_message_item_root);
            icon = view.findViewById(R.id.iv_message_icon);
            redDot = view.findViewById(R.id.v_message_red_dot);
            title = view.findViewById(R.id.tv_message_title);
            msgTime = view.findViewById(R.id.tv_message_msg_time);
            content = view.findViewById(R.id.tv_message_content);
            subContent1 = view.findViewById(R.id.tv_message_sub_content1);
            subContent2 = view.findViewById(R.id.tv_message_sub_content2);
            businessTime = view.findViewById(R.id.tv_message_business_time);
            action1 = view.findViewById(R.id.tv_message_action1);
            action2 = view.findViewById(R.id.tv_message_action2);
        }
    }

    public interface MessageOnItemLongClickListener {
        void onItemLongClick(Message message);
    }
}