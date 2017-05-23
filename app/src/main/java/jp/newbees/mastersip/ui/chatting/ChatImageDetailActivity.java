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
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.ImageChatItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.DownloadImagePresenter;
import jp.newbees.mastersip.ui.CallActivity;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ducpv on 2/9/17.
 */

public class ChatImageDetailActivity extends CallActivity implements DownloadImagePresenter.DownloadImageView {

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
    @BindView(R.id.progress_wheel)
    ProgressWheel prwImageLoading;

    private DownloadImagePresenter downloadImagePresenter;
    private ImageChatItem imageChatItem;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_image_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        downloadImagePresenter = new DownloadImagePresenter(this, this);

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
                showConfirmDownloadImageDialog(Constant.API.DOWN_IMAGE_CHAT);
            case R.id.txt_report:
            default:
                break;
        }
    }

    @Override
    public void didDownloadImage() {
        handleDownloadImage(imageChatItem.getImageItem().getOriginUrl());
    }

    @Override
    public void didDownloadImageError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(this, errorCode, errorMessage);
    }

    @Override
    protected void handleRequestDownloadImage() {
        UserItem currentUser = ConfigManager.getInstance().getCurrentUser();
        int minPoint = ConfigManager.getInstance().getMinPointDownImageChat();
        if (minPoint > currentUser.getCoin() && currentUser.isMale()) {
            showDialogMissingPoint();
        } else {
            showLoading();
            downloadImagePresenter.downloadImage(imageChatItem.getMessageId(),
                    Constant.API.DOWN_IMAGE_CHAT);
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
    public static void startActivity(Activity activity, ImageChatItem imageChatItem) {
        Intent intent = new Intent(activity, ChatImageDetailActivity.class);
        intent.putExtra(IMAGE_CHAT_ITEM, imageChatItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bot, R.anim.exit_to_top);
    }
}
