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
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.ImageUtils;

/**
 * Created by ducpv on 12/13/16.
 */

public class RegisterProfileFemaleActivity extends RegisterBaseActivity implements View.OnClickListener,
        SelectAvatarDialog.OnSelectAvatarDiaLogClick, SelectionDialog.OnSelectionDialogClick,
        UpdateRegisterProfilePresenter.View, UploadImagePresenter.View {

    private static final long TIME_DELAY = 2000;
    private Uri pickedImage;
    private Bitmap bitmapAvatar;
    private int imageId;

    private ArrayList<SelectionItem> femaleJobItems;
    private ArrayList<SelectionItem> typeItems;
    private ArrayList<SelectionItem> availableTimeItems;

    private SelectionItem provinceItem;
    private SelectionItem jobItem;
    private SelectionItem typeItem;
    private SelectionItem availableTimeItem;

    private InputDataType inputDataType;
    private SelectDataType selectDataType;

    private UserItem userItem;

    private UpdateRegisterProfilePresenter updateRegisterProfilePresenter;
    private UploadImagePresenter uploadImagePresenter;

    enum InputDataType {
        TYPE_OF_MEN, CHARM_POINT, STATUS;
    }

    enum SelectDataType {
        JOB, TYPE, AVAILABLE_TIME;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_register_profile_female;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initHeader(getString(R.string.register_profile));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        showMessageDialog(getString(R.string.register_success), getString(R.string.mess_input_profile)
                , "", false);

        updateRegisterProfilePresenter = new UpdateRegisterProfilePresenter(getApplicationContext(),
                this);
        uploadImagePresenter = new UploadImagePresenter(getApplicationContext(), this);

        femaleJobItems = new ArrayList<>();
        typeItems = new ArrayList<>();
        availableTimeItems = new ArrayList<>();

        userItem = getUserItem();

        String[] femaleJobs = getResources().getStringArray(R.array.female_job);
        for (int i = 0; i < femaleJobs.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + 1, femaleJobs[i]);
            femaleJobItems.add(selectionItem);
        }

        String[] types = getResources().getStringArray(R.array.type);
        for (int i = 0; i < types.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + 1, types[i]);
            typeItems.add(selectionItem);
        }

        String[] availableTimes = getResources().getStringArray(R.array.rest_time);
        for (int i = 0; i < availableTimes.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + 1, availableTimes[i]);
            availableTimeItems.add(selectionItem);
        }

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

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_type,
            R.id.layout_type_of_men, R.id.layout_charm_point, R.id.layout_available_time,
            R.id.layout_status, R.id.btn_complete_register, R.id.img_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.img_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, true);
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
            case R.id.layout_type:
                selectType();
                break;
            case R.id.layout_type_of_men:
                inputTypeOfMen();
                break;
            case R.id.layout_charm_point:
                inputCharmPoint();
                break;
            case R.id.layout_available_time:
                selectAvailableTime();
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

        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        doRegister();
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
                    if (provinceItem.getTitle().length() >0) {
                        txtArea.setText(provinceItem.getTitle());
                    }
                }
        }
    }

    @Override
    public void onDeleteImageClick() {
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
        switch (selectDataType) {
            case TYPE:
                typeItem = typeItems.get(position);
                txtType.setText(typeItem.getTitle());
                break;
            case JOB:
                jobItem = femaleJobItems.get(position);
                txtProfession.setText(jobItem.getTitle());
                break;
            case AVAILABLE_TIME:
                availableTimeItem = availableTimeItems.get(position);
                txtAvaiableTime.setText(availableTimeItem.getTitle());
                break;
        }
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

        if (typeItem != null) {
            userItem.setTypeGirl(typeItem);
        }

        String typeOfMen = txtTypeOfMenContent.getText().toString();
        if (typeOfMen.length() > 0) {
            userItem.setTypeBoy(typeOfMen);
        }

        String charmPoint = txtCharmPointContent.getText().toString();
        if (charmPoint.length() > 0) {
            userItem.setCharmingPoint(charmPoint);
        }

        if (availableTimeItem != null) {
            userItem.setAvailableTimeItem(availableTimeItem);
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

    private void inputStatus() {
        inputDataType = InputDataType.STATUS;
        goToInputDataActivity(getString(R.string.status), txtStatusContent.getText().toString());
    }

    private void selectAvailableTime() {
        selectDataType = SelectDataType.AVAILABLE_TIME;
        if (availableTimeItem == null) {
            availableTimeItem = new SelectionItem();
        }
        openSelectionDialog(getString(R.string.available_time), availableTimeItems, availableTimeItem);
    }

    private void inputCharmPoint() {
        inputDataType = InputDataType.CHARM_POINT;

        goToInputDataActivity(getString(R.string.charm_point), txtCharmPointContent.getText().toString());
    }

    private void inputTypeOfMen() {
        inputDataType = InputDataType.TYPE_OF_MEN;
        goToInputDataActivity(getString(R.string.type_of_men), txtTypeOfMenContent.getText().toString());
    }

    private void selectType() {
        selectDataType = SelectDataType.TYPE;
        if (typeItem == null) {
            typeItem = new SelectionItem();
        }
        openSelectionDialog(getString(R.string.type), typeItems, typeItem);
    }

    private void selectJob() {
        selectDataType = SelectDataType.JOB;
        if (jobItem == null) {
            jobItem = new SelectionItem();
        }
        openSelectionDialog(getString(R.string.profession), femaleJobItems, jobItem);
    }

    private void openSelectionDialog(String title, ArrayList<SelectionItem> data,
                                     SelectionItem selectedItem) {

        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                data, title, selectedItem);
    }

    private void goToInputDataActivity(String title, String textContent) {
        Intent intent = new Intent(getApplicationContext(), InputActivity.class);
        intent.putExtra(InputActivity.TITLE, title);
        intent.putExtra(InputActivity.TEXT_CONTENT, textContent);

        startActivityForResult(intent, InputActivity.INPUT_ACTIVITY_REQUEST_CODE);
    }

    private void handleDataInput(Intent data) {
        String content = data.getStringExtra(InputActivity.INPUT_DATA);

        switch (inputDataType) {
            case TYPE_OF_MEN:
                txtTypeOfMenContent.setText(content);
                showTextViewIfHasData(content, imgDividerTypeOfMen, txtTypeOfMenContent);
                break;
            case CHARM_POINT:
                txtCharmPointContent.setText(content);
                showTextViewIfHasData(content, imgDividerCharmPoint, txtCharmPointContent);
                break;
            case STATUS:
                txtStatusContent.setText(content);
                showTextViewIfHasData(content, imgDividerStatus, txtStatusContent);
                break;
            default:
                break;
        }
    }

    private void showTextViewIfHasData(String content, ImageView dividerLine,
                                       HiraginoTextView textView) {
        if (content.length() > 0) {
            dividerLine.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            dividerLine.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
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

    @BindView(R.id.img_select_avatar)
    ImageView imgSelectAvatar;
    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.edt_nickname)
    HiraginoEditText edtNickname;
    @BindView(R.id.txt_area)
    HiraginoTextView txtArea;
    @BindView(R.id.layout_area)
    RelativeLayout layoutArea;
    @BindView(R.id.txt_profession)
    HiraginoTextView txtProfession;
    @BindView(R.id.layout_profession)
    RelativeLayout layoutProfession;
    @BindView(R.id.txt_type)
    HiraginoTextView txtType;
    @BindView(R.id.layout_type)
    RelativeLayout layoutType;
    @BindView(R.id.layout_type_of_men)
    RelativeLayout layoutTypeOfMen;
    @BindView(R.id.txt_type_of_men_content)
    HiraginoTextView txtTypeOfMenContent;
    @BindView(R.id.layout_charm_point)
    RelativeLayout layoutCharmPoint;
    @BindView(R.id.txt_charm_point_content)
    HiraginoTextView txtCharmPointContent;
    @BindView(R.id.txt_available_time)
    HiraginoTextView txtAvaiableTime;
    @BindView(R.id.layout_available_time)
    RelativeLayout layoutAvailableTime;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.txt_status_content)
    HiraginoTextView txtStatusContent;
    @BindView(R.id.btn_complete_register)
    RippleView btnCompleteRegister;
    @BindView(R.id.img_divider_type_of_men)
    ImageView imgDividerTypeOfMen;
    @BindView(R.id.img_divider_charm_point)
    ImageView imgDividerCharmPoint;
    @BindView(R.id.img_divider_status)
    ImageView imgDividerStatus;

}
