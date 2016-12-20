package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.InputActivity;
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ImageUtils;

/**
 * Created by ducpv on 12/13/16.
 */

public class RegisterProfileFemaleActivity extends BaseActivity implements View.OnClickListener,
        SelectAvatarDialog.OnSelectAvatarDiaLogClick, SelectionDialog.OnSelectionDialogClick {

    private Uri pickedImage;
    private Bitmap bitmapAvatar;

    private ArrayList<SelectionItem> femaleJobItems;
    private ArrayList<SelectionItem> typeItems;
    private ArrayList<SelectionItem> availableTimeItems;

    private InputDataType inputDataType;
    private SelectDateType selectDataType;

    enum InputDataType {
        TYPE_OF_MEN, CHARM_POINT, STATUS;
    }

    enum SelectDateType {
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

        femaleJobItems = new ArrayList<>();
        typeItems = new ArrayList<>();
        availableTimeItems = new ArrayList<>();

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
                selectLocation();
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
                Intent intent = new Intent(getApplicationContext(), TopActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void selectLocation() {
        Intent intent = new Intent(getApplicationContext(), PickLocationActivity.class);
        startActivityForResult(intent, PickLocationActivity.PICK_LOCATION_REQUEST_CODE);
    }

    private void inputStatus() {
        inputDataType = InputDataType.STATUS;
        goToInputDataActivity(getString(R.string.status));
    }

    private void selectAvailableTime() {
        selectDataType = SelectDateType.AVAILABLE_TIME;
        openSelectionDialog(getString(R.string.available_time), availableTimeItems);
    }

    private void inputCharmPoint() {
        inputDataType = InputDataType.CHARM_POINT;
        goToInputDataActivity(getString(R.string.charm_point));
    }

    private void inputTypeOfMen() {
        inputDataType = InputDataType.TYPE_OF_MEN;
        goToInputDataActivity(getString(R.string.type_of_men));
    }

    private void selectType() {
        selectDataType = SelectDateType.TYPE;
        openSelectionDialog(getString(R.string.type), typeItems);
    }

    private void selectJob() {
        selectDataType = SelectDateType.JOB;
        openSelectionDialog(getString(R.string.profession), femaleJobItems);
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

        switch (inputDataType) {
            case TYPE_OF_MEN:
                imgDividerTypeOfMen.setVisibility(View.VISIBLE);
                txtTypeOfMenContent.setVisibility(View.VISIBLE);
                txtTypeOfMenContent.setText(content);
                break;
            case CHARM_POINT:
                imgDividerCharmPoint.setVisibility(View.VISIBLE);
                txtCharmPointContent.setVisibility(View.VISIBLE);
                txtCharmPointContent.setText(content);
                break;
            case STATUS:
                imgDividerStatus.setVisibility(View.VISIBLE);
                txtStatusContent.setVisibility(View.VISIBLE);
                txtStatusContent.setText(content);
                break;
            default:
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
        }
    }

    @Override
    public void onDeleteImageClick() {
        showMessageDialog("", getString(R.string.mess_delete_image_success), "", true);
        hideAvatar();
    }

    @Override
    public void onItemSelected(int position) {
        switch (selectDataType) {
            case TYPE:
                txtType.setText(typeItems.get(position).getTitle());
                break;
            case JOB:
                txtProfession.setText(femaleJobItems.get(position).getTitle());
                break;
            case AVAILABLE_TIME:
                txtAvaiableTime.setText(availableTimeItems.get(position).getTitle());
                break;
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
