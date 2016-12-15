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

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectAvatarDialog;
import jp.newbees.mastersip.ui.top.TopActivity;
import jp.newbees.mastersip.utils.ImageUtils;

/**
 * Created by ducpv on 12/13/16.
 */

public class RegisterProfileFemaleActivity extends BaseActivity implements View.OnClickListener,
        SelectAvatarDialog.OnSelectAvatarDiaLogClick {

    private Uri pickedImage;
    private Bitmap bitmapAvatar;

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
    }

    @OnClick({R.id.img_select_avatar, R.id.layout_area, R.id.layout_profession, R.id.layout_type,
            R.id.layout_type_of_men, R.id.layout_charm_point, R.id.layout_available_time,
            R.id.layout_status, R.id.img_complete_register, R.id.img_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_select_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, false);
                break;
            case R.id.img_avatar:
                SelectAvatarDialog.showDialogSelectAvatar(this, true);
                break;
            case R.id.layout_area:
                break;
            case R.id.layout_profession:
                break;
            case R.id.layout_type:
                break;
            case R.id.layout_type_of_men:
                break;
            case R.id.layout_charm_point:
                break;
            case R.id.layout_available_time:
                break;
            case R.id.layout_status:
                break;
            case R.id.img_complete_register:
                Intent intent = new Intent(getApplicationContext(), TopActivity.class);
                startActivity(intent);
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
                } else if (resultCode == RESULT_CANCELED) {
                    showAvatar();
                    imgAvatar.setImageURI(null);
                    imgAvatar.setImageURI(pickedImage);
                }
        }
    }

    @Override
    public void onDeleteImageClick() {
        showMessageDialog("", getString(R.string.mess_delete_image_success), "", true);
        hideAvatar();
    }

    private void handleImageCropped(Intent data) {
        byte[] result = data.getByteArrayExtra(CropImageActivity.IMAGE_CROPPED);

        Bitmap bitmap = BitmapFactory.decodeByteArray(
                result, 0, result.length);

        showAvatar();

        imgAvatar.setImageBitmap(bitmap);
        ;
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
    @BindView(R.id.txt_avaiable_time)
    HiraginoTextView txtAvaiableTime;
    @BindView(R.id.layout_available_time)
    RelativeLayout layoutAvailableTime;
    @BindView(R.id.layout_status)
    RelativeLayout layoutStatus;
    @BindView(R.id.txt_status_content)
    HiraginoTextView txtStatusContent;
    @BindView(R.id.img_complete_register)
    ImageView imgCompleteRegister;
}
