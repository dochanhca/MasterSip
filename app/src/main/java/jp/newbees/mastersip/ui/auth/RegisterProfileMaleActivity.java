package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;

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
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.InputActivity;
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.ImageUtils;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterProfileMaleActivity extends BaseActivity implements View.OnClickListener,
        SelectAvatarDialog.OnSelectAvatarDiaLogClick, SelectionDialog.OnSelectionDialogClick,
        UploadImagePresenter.View, UpdateRegisterProfilePresenter.View {

    private Uri pickedImage;
    private Bitmap bitmapAvatar;

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
    }

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_status,
            R.id.btn_complete_register, R.id.img_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.img_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, true);
                break;
            case R.id.layout_area:
                selectLocation();
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
            case SelectAvatarDialog.PICK_AVATAR_CAMERA:
                if (resultCode == RESULT_OK) {
                    handleImageFromCamera();
                }
                break;
            case SelectAvatarDialog.PICK_AVATAR_GALLERY:
                if (resultCode == RESULT_OK) {
                    pickedImage = data.getData();
                    handleImageFromGallery();
                }
                break;
            case SelectAvatarDialog.CROP_IMAGE:
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
                    txtArea.setText(provinceItem.getTitle());
                }
        }
    }

    @Override
    public void onDeleteImageClick() {
        //
        showMessageDialog("", getString(R.string.mess_delete_image_success), "", true);
        hideAvatar();
    }

    @Override
    public void onItemSelected(int position) {
        jobItem = maleJobItems.get(position);
        txtProfession.setText(jobItem.getTitle());
    }


    @Override
    public void onUpdateRegisterProfileSuccess(UserItem userItem) {
        disMissLoading();
        saveLoginState();
        startTopScreenWithNewTask();
    }

    @Override
    public void onUpdateRegisterProfileFailure(int errorCode, String errorMessage) {
        disMissLoading();
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUploadImageSuccess(ImageItem imageItem) {
        userItem.setAvatarItem(imageItem);
        doRegister();
    }

    @Override
    public void onUploadImageFailure(int errorCode, String errorMessage) {
        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        doRegister();
    }

    private void selectLocation() {
        Intent intent = new Intent(getApplicationContext(), PickLocationActivity.class);
        startActivityForResult(intent, PickLocationActivity.PICK_LOCATION_REQUEST_CODE);
    }

    private void inputStatus() {
        goToInputDataActivity(getString(R.string.status));
    }

    private void selectJob() {
        openSelectionDialog(getString(R.string.profession), maleJobItems);
    }

    private void openSelectionDialog(String title, ArrayList<SelectionItem> data) {
        SelectionDialog selectionDialog = new SelectionDialog();

        Bundle bundle = new Bundle();
        bundle.putString(SelectionDialog.DIALOG_TILE, title);
        bundle.putParcelableArrayList(SelectionDialog.LIST_SELECTION, data);

        selectionDialog.setArguments(bundle);
        selectionDialog.show(getFragmentManager(), "SelectionDialog");
    }

    private void goToInputDataActivity(String title) {
        Intent intent = new Intent(getApplicationContext(), InputActivity.class);
        intent.putExtra(InputActivity.TITLE, title);

        startActivityForResult(intent, InputActivity.INPUT_ACTIVITY_REQUEST_CODE);
    }

    private void handleDataInput(Intent data) {
        String content = data.getStringExtra(InputActivity.INPUT_DATA);

        if (content.length() <= 0) {
            return;
        }

        imgDividerStatus.setVisibility(View.VISIBLE);
        txtStatusContent.setVisibility(View.VISIBLE);
        txtStatusContent.setText(content);
    }


    private void handleImageCropped(Intent data) {
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                result, 0, result.length);

        showAvatar();
        imgAvatar.setImageBitmap(bitmap);
    }

    private void handleImageFromCamera() {
        File outFile = new File(Environment.getExternalStorageDirectory() + SelectAvatarDialog.AVATAR_NAME);
        if (!outFile.exists()) {
            Toast.makeText(getBaseContext(), "Error while capturing image", Toast.LENGTH_SHORT).show();
        } else {
            pickedImage = Uri.fromFile(outFile);
            gotoCropImageScreen(pickedImage);
        }
    }

    private void handleImageFromGallery() {
        getImageFilePath();
    }

    private void getImageFilePath() {
        if (pickedImage.toString().startsWith("content://com.google.android.apps.photos.content")) {
            pickedImage = ImageUtils.getImageUrlWithAuthority(this, pickedImage);
        }
        gotoCropImageScreen(pickedImage);
    }

    private void gotoCropImageScreen(Uri imagePath) {
        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);

        intent.putExtra(CropImageActivity.IMAGE_URI, imagePath);

        startActivityForResult(intent, SelectAvatarDialog.CROP_IMAGE);
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

        if (imgAvatar.getDrawable() != null) {
            showLoading();
            Bitmap avatar = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
            InputStream inputStream = ImageUtils.convertToInputStream(avatar);
            uploadImagePresenter.upLoadImage(userItem.getUserId(), UploadImageTask.UPLOAD_FOR_TEMPLATE,
                    inputStream);
        } else {
            doRegister();
        }
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
        } else if (provinceItem == null) {
            showMessageDialog("", getString(R.string.err_require_field_empty), "", false);
        } else {
            isDataValid = true;
        }

        return isDataValid;
    }

    private void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    private void saveLoginState() {
        getEditor().putBoolean(Constant.Application.LOGIN_FLAG, true);
        getEditor().commit();
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
