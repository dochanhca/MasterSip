package jp.newbees.mastersip.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.bumptech.glide.Glide;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

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
import jp.newbees.mastersip.ui.auth.CropImageActivity;
import jp.newbees.mastersip.ui.auth.PickLocationActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.ImageUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 1/4/17.
 */

public abstract class ProfileBaseActivity extends BaseActivity implements
        UpdateRegisterProfilePresenter.View, UploadImagePresenter.View,
        TextDialog.OnTextDialogPositiveClick, SelectImageDialog.OnSelectAvatarDiaLogClick {

    protected static final String START_MODE = "START_MODE";
    public static final int MODE_REGISTER = 1;
    public static final int MODE_UPDATE = 2;

    @BindView(R.id.img_select_avatar)
    protected ImageView imgSelectAvatar;
    @BindView(R.id.img_avatar)
    protected ImageView imgAvatar;
    @BindView(R.id.edt_nickname)
    protected HiraginoEditText edtNickname;
    @BindView(R.id.layout_nickname)
    protected RelativeLayout layoutNickname;
    @BindView(R.id.txt_area)
    protected HiraginoTextView txtArea;
    @BindView(R.id.layout_area)
    protected RelativeLayout layoutArea;
    @BindView(R.id.txt_profession)
    protected HiraginoTextView txtProfession;
    @BindView(R.id.layout_profession)
    protected RelativeLayout layoutProfession;
    @BindView(R.id.layout_status)
    protected RelativeLayout layoutStatus;
    @BindView(R.id.txt_status_content)
    protected HiraginoTextView txtStatusContent;
    @BindView(R.id.rv_complete_register)
    protected RippleView btnCompleteRegister;
    @BindView(R.id.btn_save_profile)
    protected Button btnSaveProfile;
    @BindView(R.id.img_divider_status)
    protected ImageView imgDividerStatus;
    @BindView(R.id.txt_dob)
    protected HiraginoTextView txtDOB;
    @BindView(R.id.layout_dob)
    ViewGroup layoutDOB;
    @BindView(R.id.img_divider_dob)
    ImageView imgDividerDOB;
    @BindView(R.id.img_mask_approving)
    protected ImageView imgMaskApproving;
    @BindView(R.id.txt_approving)
    protected TextView txtApproving;

    protected UpdateRegisterProfilePresenter updateRegisterProfilePresenter;
    protected UploadImagePresenter uploadImagePresenter;
    protected UserItem userItem;

    protected int mode;
    protected SelectionItem jobItem;
    protected static final long TIME_DELAY = 2000;

    protected String dateSendToServer = "";
    private String mDOB = "";
    private Date defaultDate;
    private Date currentDate;
    private int myAge = 0;

    private Uri pickedImage;
    private SelectionItem provinceItem;
    private boolean avatarDeleted;


    private SlideDateTimeListener onDateSelected = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            defaultDate = date;
            mDOB = DateTimeUtils.JAPAN_DATE_FORMAT.format(date);
            dateSendToServer = DateTimeUtils.ENGLISH_DATE_FORMAT.format(date);
            txtDOB.setText(mDOB);
            myAge = DateTimeUtils.subtractDateToYear(date, currentDate);
        }
    };

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        userItem = ConfigManager.getInstance().getCurrentUser();

        mode = getIntent().getIntExtra(START_MODE, MODE_REGISTER);
        if (mode == MODE_REGISTER) {
            initHeader(getString(R.string.register_profile));
            showMessageDialog(getString(R.string.register_success), getString(R.string.mess_input_profile)
                    , "", false);
            layoutDOB.setVisibility(View.GONE);
            imgDividerDOB.setVisibility(View.GONE);
        } else {
            initHeader(getString(R.string.edit_profile));
            btnSaveProfile.setText(getString(R.string.save_changes));
        }
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {


        updateRegisterProfilePresenter = new UpdateRegisterProfilePresenter(getApplicationContext(),
                this);
        uploadImagePresenter = new UploadImagePresenter(getApplicationContext(), this);

        Calendar calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        updateData();
    }

    /**
     * On Confirm Delete Image Click Ok listener
     *
     * @param requestCode
     */
    @Override
    public void onTextDialogOkClick(int requestCode) {
        avatarDeleted = true;
        showMessageDialog("", getString(R.string.mess_delete_image_success), "", true);
        hideAvatar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                disMissMessageDialog();
            }
        }, TIME_DELAY);
    }

    /**
     * Select delete avatar option
     */
    @Override
    public void onDeleteImageClick() {
        confirmDeleteAvatar();
    }

    @Override
    public void onUpdateRegisterProfileSuccess(UserItem userItem) {
        disMissLoading();
        if (mode == MODE_REGISTER) {
            startTopScreenWithNewTask();
        } else {
            setResult(RESULT_OK);
            this.finish();
        }
    }

    @Override
    public void onUpdateRegisterProfileFailure(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onUploadImageSuccess(ImageItem imageItem) {
        userItem.setAvatarItem(imageItem);
        updateProfile();
    }

    @Override
    public void onUploadImageFailure(int errorCode, String errorMessage) {
        Logger.e(TAG, "error code = " + errorCode + " : " + errorMessage);
        updateProfile();
    }

    @Override
    public void onDeleteAvatarSuccess() {
        userItem.setAvatarItem(null);
        updateProfile();
    }

    @Override
    public void onDeleteAvatarFailure(int errorCode, String errorMessage) {
        Logger.e(TAG, "error code = " + errorCode + " : " + errorMessage);
        updateProfile();
    }

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_status,
            R.id.rv_complete_register, R.id.img_avatar, R.id.layout_dob})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectImageDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.img_avatar:
                if (userItem.getAvatarItem().getImageStatus() == ImageItem.IMAGE_APPROVED) {
                    SelectImageDialog.showDialogSelectAvatar(this, true);
                }
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
            case R.id.rv_complete_register:
                uploadAvatarToServerIfExist();
                break;
            case R.id.layout_dob:
                openDialogDatePicker();
                break;
            default:
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
                    handleLocationPicked(data);
                }
                break;
            default:
                break;
        }
    }

    protected abstract void selectJob();

    protected abstract void updateProfile();

    protected abstract void handleDataInput(Intent data);

    protected abstract void fillProfile();

    protected abstract void inputStatus();

    protected void updateCommonProfile() {
        userItem.setUsername(edtNickname.getText().toString().trim());
        userItem.setLocation(provinceItem);

        if (jobItem != null) {
            userItem.setJobItem(jobItem);
        }

        String memo = txtStatusContent.getText().toString();
        if (memo.length() > 0) {
            userItem.setMemo(memo);
        }
        if (mode == MODE_UPDATE) {
            userItem.setDateOfBirth(dateSendToServer);
        }
    }

    protected void goToInputDataActivity(String title, String textContent) {
        Intent intent = new Intent(getApplicationContext(), InputActivity.class);
        intent.putExtra(InputActivity.TITLE, title);
        intent.putExtra(InputActivity.TEXT_CONTENT, textContent);

        startActivityForResult(intent, InputActivity.INPUT_ACTIVITY_REQUEST_CODE);
    }

    protected void fillCommonProfile() {
        provinceItem = userItem.getLocation();
        txtArea.setText(provinceItem.getTitle());

        jobItem = userItem.getJobItem();
        txtProfession.setText(jobItem.getTitle());

        txtStatusContent.setText(userItem.getMemo());
        showTextViewIfHasData(userItem.getMemo(), imgDividerStatus, txtStatusContent);
    }

    protected void showTextViewIfHasData(String content, ImageView dividerLine,
                                         HiraginoTextView textView) {
        if (content.length() > 0) {
            dividerLine.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            dividerLine.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
    }

    private void handleLocationPicked(Intent data) {
        provinceItem = data.getParcelableExtra(PickLocationActivity.PROVINCE_ITEM);
        if (provinceItem.getTitle().length() > 0) {
            txtArea.setText(provinceItem.getTitle());
        }
    }

    private void confirmDeleteAvatar() {
        String confirmDeleteAvatar = getString(R.string.confirm_delete_avatar);
        TextDialog textDialog = new TextDialog.Builder().build(confirmDeleteAvatar);
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    private void handleImageCropped(Intent data) {
        String imagePath = data.getStringExtra(CropImageActivity.IMAGE_CROPPED);
        Bitmap bitmap = ImageUtils.decodeBitmapFromFile(imagePath, Constant.Application.MAX_IMAGE_WIDTH,
                Constant.Application.MAX_CHAT_IMAGE_HEIGHT);

        showAvatar();
        imgAvatar.setImageBitmap(bitmap);
        avatarDeleted = false;
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
        if (!checkDataValid() || (mode == MODE_UPDATE && !isDOBValid())) {
            return;
        }

        if (avatarDeleted && mode == MODE_UPDATE) {
            updateRegisterProfilePresenter.deleteAvatar();
        } else if (imgAvatar.getDrawable() != null && imgMaskApproving.getVisibility() != View.VISIBLE) {
            showLoading();
            Bitmap avatar = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
            InputStream inputStream = ImageUtils.convertToInputStream(avatar);
            uploadImagePresenter.upLoadImage(userItem.getUserId(), UploadImageTask.UPLOAD_FOR_TEMPLATE,
                    inputStream);
        } else {
            updateProfile();
        }
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

    private boolean isDOBValid() {
        boolean isValid = true;
        if (txtDOB.getText().length() < 1) {
            showMessageDialog("", getString(R.string.err_dob_empty), "", false);
            isValid = false;
        } else if (myAge < Constant.Application.MIN_AGE) {
            showMessageDialog("", getString(R.string.err_age_less_than_18), "", false);
            isValid = false;
        }
        return isValid;
    }

    private void updateData() {
        edtNickname.setText(userItem.getUsername());
        handleAvatar();
        if (mode == MODE_UPDATE) {
            showDOB();
            fillProfile();
        }
    }

    private void showDOB() {
        dateSendToServer = userItem.getDateOfBirth();
        defaultDate = DateTimeUtils.convertStringToDate(dateSendToServer, DateTimeUtils.ENGLISH_DATE_FORMAT);
        mDOB = DateTimeUtils.convertDateToString(defaultDate, DateTimeUtils.JAPAN_DATE_FORMAT);
        txtDOB.setText(mDOB);
        myAge = DateTimeUtils.subtractDateToYear(defaultDate, currentDate);
    }

    private void handleAvatar() {
        ImageItem avatarItem = userItem.getAvatarItem();
        if (avatarItem != null && !"".equals(avatarItem.getOriginUrl())) {
            if (mode == MODE_UPDATE) {
                int visibility = avatarItem.getImageStatus() == ImageItem.IMAGE_APPROVED ? View.INVISIBLE : View.VISIBLE;
                imgMaskApproving.setVisibility(visibility);
                txtApproving.setVisibility(visibility);
            }
            showAvatar();
            int defaultAvatar = ConfigManager.getInstance().getImageCallerDefault();
            Glide.with(this).load(avatarItem.getOriginUrl()).asBitmap()
                    .error(defaultAvatar).placeholder(defaultAvatar).into(imgAvatar);
        }
    }

    private void openDialogDatePicker() {
        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(onDateSelected)
                .setInitialDate(defaultDate)
                .setMaxDate(currentDate)
                .build()
                .show();
    }
}
