package jp.newbees.mastersip.ui.chatting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.network.api.SendMessageRequestEnableCallTask;
import jp.newbees.mastersip.ui.CallActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ducpv on 2/9/17.
 */

public class ChatImageDetailActivity extends CallActivity {

    private static final String IMAGE_CHAT_ITEM = "IMAGE_CHAT_ITEM";

    @BindView(R.id.img_photo)
    ImageView imgPhoto;
    @BindView(R.id.txt_save_photo)
    HiraginoTextView txtSavePhoto;
    @BindView(R.id.txt_report)
    HiraginoTextView txtReport;
    @BindView(R.id.layout_bottom_action)
    LinearLayout layoutBottomAction;
    @BindView(R.id.img_close)
    ImageView imgClose;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_image_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);

        ImageChatItem imageChatItem = getIntent().getParcelableExtra(IMAGE_CHAT_ITEM);
        if (imageChatItem.isOwner()) {
            layoutBottomAction.setVisibility(View.GONE);
        } else {
            layoutBottomAction.setVisibility(View.VISIBLE);
        }
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imgPhoto, true);
        Glide.with(this).load(imageChatItem.getImageItem().getOriginUrl())
                .into(imgPhoto);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @OnClick({R.id.txt_save_photo, R.id.txt_report, R.id.img_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_save_photo:
                break;
            case R.id.txt_report:
                break;
            case R.id.img_close:
                finish();
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
    }

    /**
     * @param activity
     * @param imageChatItem
     */
    public static void startActivity(Activity activity, ImageChatItem imageChatItem) {
        Intent intent = new Intent(activity, ChatImageDetailActivity.class);
        intent.putExtra(IMAGE_CHAT_ITEM, imageChatItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bot, R.anim.exit_to_top);
    }
}
