package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.CallLogItem;
import jp.newbees.mastersip.model.HistoryCallItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by vietbq on 4/18/17.
 */

public class HistoryCallAdapter extends SectionedRecyclerViewAdapter<HistoryCallAdapter.HistoryCallViewHolder> {

    private final Context context;
    private List<CallLogItem> data;
    private HistoryCallViewHolder.OnHistoryCallClickListener historyCallClickListener;

    public HistoryCallAdapter(Context context, List<CallLogItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getSectionCount() {
        return this.data.size();
    }

    @Override
    public int getItemCount(int section) {
        return this.data.get(section).getHistoryCallItems().size();
    }

    @Override
    public void onBindHeaderViewHolder(HistoryCallViewHolder holder, int section) {
        CallLogItem historyCallItem = data.get(section);
        Date groupDate = DateTimeUtils.convertStringToDate(historyCallItem.getDate(), DateTimeUtils.ENGLISH_DATE_FORMAT);
        String sectionTitle = DateTimeUtils.getHeaderDisplayDateInChatHistory(groupDate, context);
        holder.headerHolder.txtSection.setText(sectionTitle);
    }

    @Override
    public void onBindViewHolder(HistoryCallViewHolder holder, int section, int relativePosition, int absolutePosition) {
        CallLogItem historyCallItem = data.get(section);
        holder.itemHolder.updateView(historyCallItem.getHistoryCallItems().get(relativePosition), context);
        holder.itemHolder.setOnHistoryCallClickListener(historyCallClickListener);
    }

    @Override
    public HistoryCallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_HEADER ? R.layout.section_day_item : R.layout.item_history_call, parent, false);
        return new HistoryCallViewHolder(view, viewType);
    }

    public void setOnItemClickListener(HistoryCallViewHolder.OnHistoryCallClickListener historyCallClickListener) {
        this.historyCallClickListener = historyCallClickListener;
    }

    public void clearData() {
        this.data.clear();
    }

    public void setData(List<CallLogItem> data) {
        this.data = data;
    }

    /**
     * Footprint ViewHolder
     */
    public static class HistoryCallViewHolder extends RecyclerView.ViewHolder {
        private HeaderHolder headerHolder;
        private ItemHolder itemHolder;

        /**
         * Constructor
         *
         * @param itemView View for holder Item or Header
         * @param viewType @VIEW_TYPE_HEADER or @VIEW_TYPE_ITEM
         */
        public HistoryCallViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_HEADER) {
                headerHolder = new HeaderHolder(itemView);
            } else {
                itemHolder = new ItemHolder(itemView);
            }
        }

        class HeaderHolder {
            /**
             * Title for Section
             */
            private HiraginoTextView txtSection;

            HeaderHolder(View view) {
                this.txtSection = (HiraginoTextView) view;
            }
        }

        class ItemHolder implements View.OnClickListener {

            private HiraginoTextView txtUserName;
            private HiraginoTextView txtCallDuration;
            private HiraginoTextView txtLastTimeCall;
            private Button btnVoiceCall;
            private Button btnVideoCall;
            private ImageView imgAvatar;
            private ImageView imgCallType;
            private OnHistoryCallClickListener historyCallClickListener;
            private HistoryCallItem historyCallItem;
            private View rootView;
            private View groupAction;

            ItemHolder(View view) {
                this.txtUserName = (HiraginoTextView) view.findViewById(R.id.txt_user_name);
                this.txtCallDuration = (HiraginoTextView) view.findViewById(R.id.txt_call_duration);
                this.imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
                this.txtLastTimeCall = (HiraginoTextView) view.findViewById(R.id.txt_last_time_call);
                this.btnVideoCall = (Button) view.findViewById(R.id.btn_video_call);
                this.btnVoiceCall = (Button) view.findViewById(R.id.btn_voice_call);
                this.groupAction = view.findViewById(R.id.group_action);
                this.imgCallType = (ImageView) view.findViewById(R.id.img_call_type);
                this.rootView = view;
            }

            /**
             * Update view for item
             *
             * @param historyCallItem
             * @param context
             */
            public void updateView(HistoryCallItem historyCallItem, Context context) {
                this.historyCallItem = historyCallItem;
                String lastTimeCall = DateTimeUtils.getShortTimeJapanese(this.historyCallItem.getLastTimeCallLog());
                this.txtLastTimeCall.setText(lastTimeCall);
                this.txtCallDuration.setText(this.historyCallItem.getDuration(context));
                this.imgCallType.setImageResource(this.historyCallItem.getDrawableCallLogType());
                updateCallSetting(context, this.historyCallItem.getUserItem().getSettings());
                updateStatusUser(this.historyCallItem.getUserItem().getStatus(), context);
            }

            private void updateStatusUser(int status, Context context) {
                if (status == UserItem.ACTIVE) {
                    handleViewForActiveUser(context);
                } else {
                    handleViewForQuitUser(context);
                }
            }

            private void handleViewForQuitUser(Context context) {
                this.txtUserName.setText(this.historyCallItem.getUserItem().getUsername());
                int defaultAvatar = getQuitImage();
                this.imgAvatar.setImageResource(getQuitImage());
                Glide.with(context).load("").placeholder(defaultAvatar).error(defaultAvatar).into(this.imgAvatar);
                this.groupAction.setVisibility(View.INVISIBLE);
                this.txtCallDuration.setVisibility(View.INVISIBLE);

                this.btnVideoCall.setOnClickListener(null);
                this.btnVoiceCall.setOnClickListener(null);
                this.rootView.setOnClickListener(null);
            }

            private void handleViewForActiveUser(Context context) {
                this.txtUserName.setText(this.historyCallItem.getUserItem().getUsername());
                this.groupAction.setVisibility(View.VISIBLE);
                this.txtCallDuration.setVisibility(View.VISIBLE);
                int deafaultAvatar = ConfigManager.getInstance().getImageCalleeDefault();
                Glide.with(context).load(this.historyCallItem.getUserItem().getAvatarItem().getOriginUrl()).placeholder(deafaultAvatar).error(deafaultAvatar).into(this.imgAvatar);
                this.btnVideoCall.setOnClickListener(ItemHolder.this);
                this.btnVoiceCall.setOnClickListener(ItemHolder.this);
                this.rootView.setOnClickListener(ItemHolder.this);
            }

            private void updateCallSetting(Context context, SettingItem settingItem) {
                int video = settingItem.getVideoCall() == SettingItem.ON ? R.drawable.ic_small_green_video : R.drawable.ic_small_gray_video;
                int voice = settingItem.getVoiceCall() == SettingItem.ON ? R.drawable.ic_small_green_voice : R.drawable.ic_small_gray_voice;

                int colorGreen = context.getResources().getColor(R.color.colorPrimary);
                int colorGray = context.getResources().getColor(R.color.color_deactivate_footprint);

                int colorVideo = settingItem.getVideoCall() == SettingItem.ON ? colorGreen : colorGray;
                int colorVoice = settingItem.getVoiceCall() == SettingItem.ON ? colorGreen : colorGray;

                btnVideoCall.setCompoundDrawablesWithIntrinsicBounds(0, video, 0, 0);
                btnVoiceCall.setCompoundDrawablesWithIntrinsicBounds(0, voice, 0, 0);
                btnVoiceCall.setTextColor(colorVoice);
                btnVideoCall.setTextColor(colorVideo);
            }

            /**
             * Register listener on each item history call
             *
             * @param historyCallClickListener
             */
            public void setOnHistoryCallClickListener(OnHistoryCallClickListener historyCallClickListener) {
                this.historyCallClickListener = historyCallClickListener;
            }

            @Override
            public void onClick(View view) {
                if (view == btnVoiceCall) {
                    this.historyCallClickListener.onVoiceClickListener(this.historyCallItem.getUserItem());
                } else if (view == btnVideoCall) {
                    this.historyCallClickListener.onVideoClickListener(this.historyCallItem.getUserItem());
                } else {
                    this.historyCallClickListener.onProfileClickListener(this.historyCallItem.getUserItem());
                }
            }

            private int getQuitImage() {
                int gender = ConfigManager.getInstance().getCurrentUser().getGender();
                return gender == UserItem.MALE ? R.drawable.ic_disable_female
                        : R.drawable.ic_disable_male;
            }
        }

        /**
         * Listener for each item in Footprint
         */
        public interface OnHistoryCallClickListener {
            /**
             * Callback when user clicks on Video Button
             *
             * @param userItem
             */
            void onVideoClickListener(UserItem userItem);

            /**
             * Callback when user clicks on Voice Button
             *
             * @param userItem
             */
            void onVoiceClickListener(UserItem userItem);

            /**
             * Callback when user clicks on Item
             *
             * @param userItem
             */
            void onProfileClickListener(UserItem userItem);
        }
    }
}
