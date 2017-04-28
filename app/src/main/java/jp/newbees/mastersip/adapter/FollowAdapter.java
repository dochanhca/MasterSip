package jp.newbees.mastersip.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Locale;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.FollowItem;
import jp.newbees.mastersip.model.SettingItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Utils;

import static jp.newbees.mastersip.utils.DateTimeUtils.convertStringToDate;

/**
 * Created by vietbq on 4/10/17.
 */

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolder> {

    private FollowViewHolder.OnFollowItemClickListener followItemListener;
    private final Context context;
    private FollowItem followList;

    public FollowAdapter(Context context, FollowItem followList) {
        this.context = context;
        this.followList = followList;
    }

    public void setOnItemFollowClickListener(FollowViewHolder.OnFollowItemClickListener listener) {
        this.followItemListener = listener;
    }

    public void clearData() {
        this.followList.getFollowers().clear();
    }

    public void setData(FollowItem data) {
        this.followList = data;
    }

    @Override
    public FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        return new FollowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FollowViewHolder holder, int position) {
        UserItem follower = followList.getFollowers().get(position);
        holder.itemHolder.updateView(follower, context);
        holder.itemHolder.setOnItemFollowClickListener(followItemListener);
    }

    @Override
    public int getItemCount() {
        return this.followList.getTotal();
    }

    /**
     * Footprint ViewHolder
     */
    public static class FollowViewHolder extends RecyclerView.ViewHolder {
        private ItemHolder itemHolder;

        /**
         * Constructor
         *
         * @param itemView View for holder Item
         */
        public FollowViewHolder(View itemView) {
            super(itemView);
            itemHolder = new ItemHolder(itemView);
        }

        class ItemHolder implements View.OnClickListener {
            private HiraginoTextView txtUserNameAge;
            private HiraginoTextView txtCallSetting;
            private Button btnVoiceCall;
            private Button btnVideoCall;
            private Button btnChat;
            private ImageView imgAvatar;
            private View groupAction;
            private OnFollowItemClickListener followItemListener;
            private UserItem userItem;
            private View rootView;

            ItemHolder(View view) {
                this.txtUserNameAge = (HiraginoTextView) view.findViewById(R.id.txt_user_name_age);
                this.txtCallSetting = (HiraginoTextView) view.findViewById(R.id.txt_call_setting);
                this.imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
                this.btnVideoCall = (Button) view.findViewById(R.id.btn_video_call);
                this.btnVoiceCall = (Button) view.findViewById(R.id.btn_voice_call);
                this.btnChat = (Button) view.findViewById(R.id.btn_chat);
                this.groupAction = view.findViewById(R.id.group_action);
                this.rootView = view;
            }

            /**
             * Update view for item
             *
             * @param userItem
             * @param context
             */
            public final void updateView(UserItem userItem, Context context) {
                this.userItem = userItem;
                this.txtUserNameAge.setText(userItem.getUsername());
                updateCallSetting(context, userItem.getSettings());
                updateDescriptionSettingCall(context, userItem.getLastLogin(), userItem.getSettings());
                this.updateStatusUser(this.userItem.getStatus(), context);
            }

            private void updateStatusUser(int status, Context context) {
                if (status == UserItem.ACTIVE) {
                    handleViewForActiveUser(context);
                } else {
                    handleViewForQuitUser(context);
                }
            }

            private void handleViewForQuitUser(Context context) {
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

                btnVideoCall.setCompoundDrawablesWithIntrinsicBounds(0, video, 0, 0);
                btnVoiceCall.setCompoundDrawablesWithIntrinsicBounds(0, voice, 0, 0);
                btnVoiceCall.setTextColor(colorVoice);
                btnVideoCall.setTextColor(colorVideo);
            }

            /**
             * Update description setting call
             *
             * @param context
             * @param lastLogin
             * @param settingItem
             */
            public void updateDescriptionSettingCall(Context context, String lastLogin, SettingItem settingItem) {
                boolean enableVideo = settingItem.getVideoCall() == SettingItem.ON ? true : false;
                boolean enableVoice = settingItem.getVoiceCall() == SettingItem.ON ? true : false;

                String description;
                if (enableVideo && enableVoice) {
                    description = context.getString(R.string.allow_video_voice_call);
                } else if (enableVideo) {
                    description = context.getString(R.string.allow_video_only);
                } else if (enableVoice) {
                    description = context.getString(R.string.allow_voice_only);
                } else {
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
             *
             * @param followItemListener
             */
            public void setOnItemFollowClickListener(OnFollowItemClickListener followItemListener) {
                this.followItemListener = followItemListener;
            }

            @Override
            public void onClick(View view) {
                if (view == btnChat) {
                    this.followItemListener.onChatClickListener(this.userItem);
                } else if (view == btnVoiceCall) {
                    this.followItemListener.onVoiceClickListener(this.userItem);
                } else if (view == btnVideoCall) {
                    this.followItemListener.onVideoClickListener(this.userItem);
                } else {
                    this.followItemListener.onProfileClickListener(this.userItem);
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
        public interface OnFollowItemClickListener {
            /**
             * Callback when user clicks on Chat Button
             *
             * @param userItem
             */
            void onChatClickListener(UserItem userItem);

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
