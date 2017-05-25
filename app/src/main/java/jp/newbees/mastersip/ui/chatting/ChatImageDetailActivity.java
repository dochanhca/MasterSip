package jp.newbees.mastersip.ui.chatting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.pnikosis.materialishprogress.ProgressWheel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.HandleImageActivity;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ducpv on 2/9/17.
 */

public class ChatImageDetailActivity extends HandleImageActivity {

    private static final String IMAGE_CHAT_ITEM = "IMAGE_CHAT_ITEM";
    private static final String USER_ID = "USER_ID";

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
    @BindView(R.id.progress_wheel)
    ProgressWheel prwImageLoading;

    private ImageChatItem imageChatItem;
    private String userId;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_image_detail;
    }

    @Override
    protected int getImageId() {
        return imageChatItem.getMessageId();
    }

    @Override
    protected int getReportImageType() {
        return Constant.API.REPORT_IMAGE_CHAT;
    }

    @Override
    protected int getDownloadImageType() {
        return Constant.API.DOWN_IMAGE_CHAT;
    }

    @Override
    protected int getMinPoint() {
        return ConfigManager.getInstance().getMinPointDownImageChat();
    }

    @Override
    protected String getImagePath() {
        return imageChatItem.getImageItem().getOriginUrl();
    }

    @Override
    protected String getUserId() {
        return userId;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra(USER_ID);
        imageChatItem = getIntent().getParcelableExtra(IMAGE_CHAT_ITEM);
        if (imageChatItem.isOwner()) {
            layoutBottomAction.setVisibility(View.GONE);
        } else {
            layoutBottomAction.setVisibility(View.VISIBLE);
        }
        loadImage();
    }

    @OnClick({R.id.txt_save_photo, R.id.txt_report, R.id.img_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
                break;
            case R.id.txt_save_photo:
                showConfirmDownloadImageDialog();
                break;
            case R.id.txt_report:
                showLoading();
                downloadAndReportPresenter.getListReportReason(Constant.API.REPORT_IMAGE_CHAT);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bot);
    }

    private void loadImage() {
        new PhotoViewAttacher(imgPhoto, true);
        Glide.with(this).load(imageChatItem.getImageItem().getOriginUrl())
                .asBitmap().atMost()
                .thumbnail(0.1f)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imgPhoto.setImageBitmap(resource);
                        prwImageLoading.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * @param activity
     * @param imageChatItem
     */
    public static void startActivity(Activity activity, ImageChatItem imageChatItem, String userId) {
        Intent intent = new Intent(activity, ChatImageDetailActivity.class);
        intent.putExtra(IMAGE_CHAT_ITEM, imageChatItem);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bot, R.anim.exit_to_top);
    }
}
