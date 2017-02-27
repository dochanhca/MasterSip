package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.UploadImageTask;
import jp.newbees.mastersip.presenter.auth.UpdateRegisterProfilePresenter;
import jp.newbees.mastersip.presenter.auth.UploadImagePresenter;
import jp.newbees.mastersip.ui.InputActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.ImageUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterProfileMaleActivity extends RegisterBaseActivity implements View.OnClickListener,
        SelectImageDialog.OnSelectAvatarDiaLogClick, SelectionDialog.OnSelectionDialogClick,
        UploadImagePresenter.View, UpdateRegisterProfilePresenter.View, TextDialog.OnTextDialogClick {

    private static final long TIME_DELAY = 2000;
    private Uri pickedImage;

    private ArrayList<SelectionItem> maleJobItems;

    private SelectionItem provinceItem;
    private SelectionItem jobItem;

    private UserItem userItem;

    private UpdateRegisterProfilePresenter updateRegisterProfilePresenter;
    private UploadImagePresenter uploadImagePresenter;


    @Override
    protected int layoutId() {
        return R.layout.activity_register_profile_male;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initHeader(getString(R.string.register_profile));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        userItem = getUserItem();

        updateRegisterProfilePresenter = new UpdateRegisterProfilePresenter(getApplicationContext(),
                this);
        uploadImagePresenter = new UploadImagePresenter(getApplicationContext(), this);

        maleJobItems = new ArrayList<>();

        String[] maleJobs = getResources().getStringArray(R.array.male_job);
        for (int i = 0; i < maleJobs.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + 1, maleJobs[i]);
            maleJobItems.add(selectionItem);
        }

        showMessageDialog(getString(R.string.register_success), getString(R.string.mess_input_profile)
                , "", false);

        handleFacebookAvatar();
    }

    private void handleFacebookAvatar() {
        if (userItem.getAvatarItem() != null && userItem.getFacebookId() != null) {
            this.showAvatar();
            int defaultAvatar = ConfigManager.getInstance().getImageCallerDefault();
            Glide.with(this).load(userItem.getAvatarItem().getOriginUrl()).asBitmap()
                    .error(defaultAvatar).placeholder(defaultAvatar).into(imgAvatar);
        }
    }

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_status,
            R.id.btn_complete_register, R.id.img_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectImageDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.img_avatar:
                SelectImageDialog.showDialogSelectAvatar(this, true);
                break;
            case R.id.layout_area:
                if (provinceItem == null) {
                    provinceItem = new SelectionItem();
                }
                PickLocationActivity.startActivityPickLocation(this, provinceItem);
                break;
            case R.id.layout_profession:
                selectJob();
                break;
            case R.id.layout_status:
                inputStatus();
                break;
            case R.id.btn_complete_register:
                uploadAvatarToServerIfExist();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SelectImageDialog.PICK_AVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    handleImageFromCamera();
                }
                break;
            case SelectImageDialog.PICK_AVATAR_GALLERY:
                if (resultCode == RESULT_OK) {
                    pickedImage = data.getData();
                    handleImageFromGallery();
                }
                break;
            case SelectImageDialog.CROP_IMAGE:
                if (resultCode == RESULT_OK) {
                    handleImageCropped(data);
                }
                break;
            case InputActivity.INPUT_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    handleDataInput(data);
                }
                break;
            case PickLocationActivity.PICK_LOCATION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    provinceItem = data.getParcelableExtra(PickLocationActivity.PROVINCE_ITEM);
                    if (provinceItem.getTitle().length() > 0) {
                        txtArea.setText(provinceItem.getTitle());
                    }
                }
        }
    }

    /**
     * Select delete avatar option
     */
    @Override
    public void onDeleteImageClick() {
        confirmDeleteAvatar();
    }

    /**
     * On Confirm Delete Image Click Ok listener
     * @param requestCode
     */
    @Override
    public void onTextDialogOkClick(int requestCode) {
        showMessageDialog("", getString(R.string.mess_delete_image_success), "", true);
        hideAvatar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disMissMessageDialog();
            }
        }, TIME_DELAY);
    }

    @Override
    public void onItemSelected(int position) {
        jobItem = maleJobItems.get(position);
        txtProfession.setText(jobItem.getTitle());
    }

    @Override
    public void onUpdateRegisterProfileSuccess(UserItem userItem) {
        disMissLoading();
        startTopScreenWithNewTask();
    }

    @Override
    public void onUpdateRegisterProfileFailure(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onUploadImageSuccess(ImageItem imageItem) {
        userItem.setAvatarItem(imageItem);
        doRegister();
    }

    @Override
    public void onUploadImageFailure(int errorCode, String errorMessage) {
        Logger.e(TAG, "error code = " + errorCode + " : " + errorMessage);
        doRegister();
    }

    private void inputStatus() {
        goToInputDataActivity(getString(R.string.status),
                txtStatusContent.getText().toString());
    }

    private void selectJob() {
        if (jobItem == null) {
            jobItem = new SelectionItem();
        }
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                maleJobItems, getString(R.string.profession), jobItem);
    }

    private void goToInputDataActivity(String title, String textContent) {
        Intent intent = new Intent(getApplicationContext(), InputActivity.class);
        intent.putExtra(InputActivity.TITLE, title);
        intent.putExtra(InputActivity.TEXT_CONTENT, textContent);

        startActivityForResult(intent, InputActivity.INPUT_ACTIVITY_REQUEST_CODE);
    }

    private void handleDataInput(Intent data) {
        String content = data.getStringExtra(InputActivity.INPUT_DATA);

        txtStatusContent.setText(content);

        showTextViewIfHasContent(content);

    }

    private void showTextViewIfHasContent(String content) {
        if (content.length() > 0) {
            imgDividerStatus.setVisibility(View.VISIBLE);
            txtStatusContent.setVisibility(View.VISIBLE);
        } else {
            imgDividerStatus.setVisibility(View.GONE);
            txtStatusContent.setVisibility(View.GONE);
        }
    }

    private void handleImageCropped(Intent data) {
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                result, 0, result.length);

        showAvatar();
        imgAvatar.setImageBitmap(bitmap);
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectImageDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            pickedImage = Uri.fromFile(outFile);
            CropImageActivity.startActivityForResult(this, pickedImage);
        }
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            pickedImage = ImageUtils.getImageUrlWithAuthority(this, pickedImage);
        }
        CropImageActivity.startActivityForResult(this, pickedImage);
    }

    private void showAvatar() {
        imgAvatar.setVisibility(View.VISIBLE);
        imgSelectAvatar.setVisibility(View.GONE);
    }

    private void hideAvatar() {
        imgAvatar.setVisibility(View.GONE);
        imgSelectAvatar.setVisibility(View.VISIBLE);
    }

    private void uploadAvatarToServerIfExist() {
        if (!checkDataValid()) {
            return;
        }

        if (hasAvatar(userItem)) {
            showLoading();
            Bitmap avatar = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
            InputStream inputStream = ImageUtils.convertToInputStream(avatar);
            uploadImagePresenter.upLoadImage(userItem.getUserId(), UploadImageTask.UPLOAD_FOR_TEMPLATE,
                    inputStream);
        } else {
            doRegister();
        }
    }

    private boolean hasAvatar(UserItem userItem) {
        if (userItem.getAvatarItem() == null ||
                (userItem.getAvatarItem().getThumbUrl().length() == 0 &&
                        userItem.getAvatarItem().getOriginUrl().length() == 0)) {
            return false;
        }
        return true;
    }

    private void doRegister() {
        userItem.setUsername(edtNickname.getText().toString().trim());
        userItem.setLocation(provinceItem);

        if (jobItem != null) {
            userItem.setJobItem(jobItem);
        }

        String memo = txtStatusContent.getText().toString();
        if (memo.length() > 0) {
            userItem.setMemo(memo);
        }

        // Send Request to server
        showLoading();
        updateRegisterProfilePresenter.updateRegisterProfile(userItem);
    }

    private boolean checkDataValid() {
        boolean isDataValid = false;
        String userName = edtNickname.getText().toString().trim();
        if (userName.length() == 0) {
            showMessageDialog("", getString(R.string.err_user_name_empty), "", false);
        } else if (provinceItem == null || provinceItem.getId() == 0) {
            showMessageDialog("", getString(R.string.err_pls_select_area), "", false);
        } else {
            isDataValid = true;
        }

        return isDataValid;
    }

    @BindView(R.id.img_select_avatar)
    ImageView imgSelectAvatar;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.edt_nickname)
    HiraginoEditText edtNickname;
    @BindView(R.id.layout_nickname)
    RelativeLayout layoutNickname;
    @BindView(R.id.txt_area)
    HiraginoTextView txtArea;
    @BindView(R.id.layout_area)
    RelativeLayout layoutArea;
    @BindView(R.id.txt_profession)
    HiraginoTextView txtProfession;
    @BindView(R.id.layout_profession)
    RelativeLayout layoutProfession;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.txt_status_content)
    HiraginoTextView txtStatusContent;
    @BindView(R.id.btn_complete_register)
    RippleView btnCompleteRegister;
    @BindView(R.id.img_divider_status)
    ImageView imgDividerStatus;
}
