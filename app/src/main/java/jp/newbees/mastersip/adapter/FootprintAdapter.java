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

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.FootprintItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

import static jp.newbees.mastersip.utils.DateTimeUtils.convertStringToDate;

/**
 * Created by vietbq on 3/29/17.
 */

public class FootprintAdapter extends SectionedRecyclerViewAdapter<FootprintAdapter.FootprintViewHolder> {
    /**
     * Context
     */
    private final Context context;
    /**
     * Data for footprint
     */
    private List<FootprintItem> data;
    /**
     * Footprint listener
     */
    private FootprintViewHolder.OnFootprintClickListener footPrintListener;

    /**
     * Adapter for foot print
     * @param context
     * @param data Non-null, must be an instance of List
     */
    public FootprintAdapter(Context context, List<FootprintItem> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * Get number of sections
     * @return
     */
    @Override
    public int getSectionCount() {
        return this.data.size();
    }

    /**
     * Get number of item in  section
     * @param section
     * @return
     */
    @Override
    public int getItemCount(int section) {
        return this.data.get(section).getUserItems().size();
    }


    @Override
    public void onBindHeaderViewHolder(FootprintViewHolder holder, int section) {
        FootprintItem footprintItem = data.get(section);
        Date groupDate = DateTimeUtils.convertStringToDate(footprintItem.getDate(), DateTimeUtils.ENGLISH_DATE_FORMAT);
        String sectionTitle = DateTimeUtils.getHeaderDisplayDateInChatHistory(groupDate, context);
        holder.headerHolder.txtSection.setText(sectionTitle);
    }

    @Override
    public void onBindViewHolder(FootprintViewHolder holder, int section, int relativePosition, int absolutePosition) {
        FootprintItem footprintItem = data.get(section);
        holder.itemHolder.updateView(footprintItem.getUserItems().get(relativePosition), context);
        holder.itemHolder.setOnFootprintClickListener(footPrintListener);
    }

    @Override
    public FootprintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType == VIEW_TYPE_HEADER ? R.layout.section_day_item : R.layout.item_footprint, parent, false);
        return new FootprintViewHolder(view, viewType);
    }

    public void setData(List<FootprintItem> data) {
        this.data = data;
    }

    public List<FootprintItem> getData() {
        return data;
    }

    /**
     * Clear all data footprint
     */
    public void clearData() {
        this.data.clear();
    }

    public void setOnItemClickListener(FootprintViewHolder.OnFootprintClickListener footPrintListener) {
        this.footPrintListener = footPrintListener;
    }

    /**
     * Footprint ViewHolder
     */
    public static class FootprintViewHolder extends RecyclerView.ViewHolder{
        private HeaderHolder headerHolder;
        private ItemHolder itemHolder;

        /**
         * Constructor
         * @param itemView View for holder Item or Header
         * @param viewType @VIEW_TYPE_HEADER or @VIEW_TYPE_ITEM
         */
        public FootprintViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_HEADER) {
                headerHolder = new HeaderHolder(itemView);
            }else {
                itemHolder = new ItemHolder(itemView);
            }
        }

        class HeaderHolder {
            /**
             * Title for Section
             */
            public HiraginoTextView txtSection;
            HeaderHolder(View view) {
                this.txtSection = (HiraginoTextView) view;
            }
        }

        class ItemHolder implements View.OnClickListener {
            public HiraginoTextView txtUserNameAge;
            public HiraginoTextView txtCallSetting;
            public HiraginoTextView txtLastFootprint;
            public Button btnVoiceCall;
            public Button btnVideoCall;
            public Button btnChat;
            public ImageView imgAvatar;
            public View separateView;
            private OnFootprintClickListener footPrintListener;
            private UserItem userItem;
            private View rootView;
            private View groupAction;

            ItemHolder(View view) {
                this.txtUserNameAge = (HiraginoTextView) view.findViewById(R.id.txt_user_name_age);
                this.txtCallSetting = (HiraginoTextView) view.findViewById(R.id.txt_call_setting);
                this.imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
                this.txtLastFootprint =  (HiraginoTextView) view.findViewById(R.id.txt_last_footprint);
                this.separateView =  view.findViewById(R.id.separate_view);
                this.btnVideoCall = (Button) view.findViewById(R.id.btn_video_call);
                this.btnVoiceCall = (Button) view.findViewById(R.id.btn_voice_call);
                this.btnChat = (Button) view.findViewById(R.id.btn_chat);
                this.groupAction = view.findViewById(R.id.group_action);
                this.rootView = view;
            }

            /**
             * Update view for item
             * @param userItem
             * @param context
             */
            public void updateView(UserItem userItem, Context context) {
                this.userItem = userItem;
                String lastFootprint = DateTimeUtils.getShorterTimeJapanese(userItem.getFootprintTime());
                this.txtLastFootprint.setText(lastFootprint);
                updateCallSetting(context, userItem.getSettings());
                updateDescriptionSettingCall(context, userItem.getLastLogin(), userItem.getSettings());
                updateStatusUser(userItem.getStatus(), context);
            }

            private void updateStatusUser(int status, Context context) {
                if (status == UserItem.ACTIVE) {
                    handleViewForActiveUser(context);
                }else {
                    handleViewForQuitUser(context);
                }
            }

            private void handleViewForQuitUser(Context context) {
                this.txtUserNameAge.setText(userItem.getUsername());
                int defaultAvatar = getQuitImage();
                this.imgAvatar.setImageResource(getQuitImage());
                Glide.with(context).load("").placeholder(defaultAvatar).error(defaultAvatar).into(this.imgAvatar);
                this.groupAction.setVisibility(View.INVISIBLE);
                this.txtCallSetting.setVisibility(View.INVISIBLE);

                this.btnVideoCall.setOnClickListener(null);
                this.btnVoiceCall.setOnClickListener(null);
                this.btnChat.setOnClickListener(null);
                this.rootView.setOnClickListener(null);
            }

            private void handleViewForActiveUser(Context context) {
                int age = DateTimeUtils.getAgeFromBirthDayServer(userItem.getDateOfBirth());
                String usernameAge = userItem.getUsername() + age + context.getString(R.string.year_old);
                this.txtUserNameAge.setText(usernameAge);
                this.groupAction.setVisibility(View.VISIBLE);
                this.txtCallSetting.setVisibility(View.VISIBLE);
                int deafaultAvatar = ConfigManager.getInstance().getImageCalleeDefault();
                Glide.with(context).load(userItem.getAvatarItem().getOriginUrl()).placeholder(deafaultAvatar).error(deafaultAvatar).into(this.imgAvatar);
                this.btnVideoCall.setOnClickListener(ItemHolder.this);
                this.btnVoiceCall.setOnClickListener(ItemHolder.this);
                this.btnChat.setOnClickListener(ItemHolder.this);
                this.rootView.setOnClickListener(ItemHolder.this);
            }

            private void updateCallSetting(Context context, SettingItem settingItem) {
                int video = settingItem.getVideoCall() == SettingItem.ON ? R.drawable.ic_small_green_video : R.drawable.ic_small_gray_video;
                int voice = settingItem.getVoiceCall() == SettingItem.ON ? R.drawable.ic_small_green_voice : R.drawable.ic_small_gray_voice;

                int colorGreen = context.getResources().getColor(R.color.colorPrimary);
                int colorGray = context.getResources().getColor(R.color.color_deactivate_footprint);

                int colorVideo = settingItem.getVideoCall() == SettingItem.ON ? colorGreen : colorGray;
                int colorVoice = settingItem.getVoiceCall() == SettingItem.ON ? colorGreen : colorGray;

                btnVideoCall.setCompoundDrawablesWithIntrinsicBounds(0,video, 0 , 0);
                btnVoiceCall.setCompoundDrawablesWithIntrinsicBounds(0,voice, 0 , 0);
                btnVoiceCall.setTextColor(colorVoice);
                btnVideoCall.setTextColor(colorVideo);
            }

            /**
             * Update description setting call
             * @param context
             * @param lastLogin
             * @param settingItem
             */
            public void updateDescriptionSettingCall(Context context,String lastLogin, SettingItem settingItem) {
                boolean enableVideo = settingItem.getVideoCall() == SettingItem.ON ? true : false;
                boolean enableVoice = settingItem.getVoiceCall() == SettingItem.ON ? true : false;

                String description;
                if (enableVideo && enableVoice) {
                    description = context.getString(R.string.allow_video_voice_call);
                } else if (enableVideo) {
                    description =  context.getString(R.string.allow_video_only);
                } else if (enableVoice) {
                    description =  context.getString(R.string.allow_voice_only);
                }else {
                    String format = context.getResources().getString(R.string.allow_chat_only);
                    String lastLoginPretty = getPrettyTimeLastLogin(context, lastLogin);
                    description = String.format(format, lastLoginPretty);
                }
                this.txtCallSetting.setText(description);
            }

            private String getPrettyTimeLastLogin(Context context, String lastLogin) {
                Locale locale = Utils.getCurrentLocale(context);
                PrettyTime prettyTime = new PrettyTime(locale);
                return prettyTime.format(convertStringToDate(lastLogin,
                        DateTimeUtils.SERVER_DATE_FORMAT));
            }

            /**
             * Register listener on each item footprint
             * @param footPrintListener
             */
            public void setOnFootprintClickListener(OnFootprintClickListener footPrintListener) {
                this.footPrintListener = footPrintListener;
            }

            @Override
            public void onClick(View view) {
                if (view == btnChat) {
                    this.footPrintListener.onChatClickListener(this.userItem);
                }else if(view == btnVoiceCall) {
                    this.footPrintListener.onVoiceClickListener(this.userItem);
                }else if (view == btnVideoCall) {
                    this.footPrintListener.onVideoClickListener(this.userItem);
                }else {
                    this.footPrintListener.onProfileClickListener(this.userItem);
                }
            }

            private int getQuitImage() {
                int gender = ConfigManager.getInstance().getCurrentUser().getGender();
                int quitImage = gender
                        == UserItem.MALE
                        ? R.drawable.ic_disable_female
                        : R.drawable.ic_disable_male;
                return quitImage;
            }
        }

        /**
         * Listener for each item in Footprint
         */
        public interface OnFootprintClickListener {
            /**
             * Callback when user clicks on Chat Button
             * @param userItem
             */
            void onChatClickListener(UserItem userItem);

            /**
             * Callback when user clicks on Video Button
             * @param userItem
             */
            void onVideoClickListener(UserItem userItem);

            /**
             * Callback when user clicks on Voice Button
             * @param userItem
             */
            void onVoiceClickListener(UserItem userItem);

            /**
             * Callback when user clicks on Item
             * @param userItem
             */
            void onProfileClickListener(UserItem userItem);
        }
    }
}
