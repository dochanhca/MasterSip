package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.ConfirmCropImageDialog;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 12/14/16.
 */

public class CropImageActivity extends BaseActivity implements CropImageView.OnCropImageCompleteListener,
        View.OnClickListener, CropImageView.OnSetImageUriCompleteListener,
        ConfirmCropImageDialog.OnDialogConfirmCropImageClick {

    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String IMAGE_CROPPED = "IMAGE_URI";

    private Uri imageUri;

    private CropImageView mCropImageView;
    private TextView txtCancel;
    private TextView txtDone;

    @Override
    protected int layoutId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mCropImageView = (CropImageView) findViewById(R.id.crop_image);
        txtCancel = (TextView) findViewById(R.id.txt_cancel);
        txtDone = (TextView) findViewById(R.id.txt_done);

        txtDone.setOnClickListener(this);
        txtCancel.setOnClickListener(this);

        mCropImageView.setAspectRatio(1, 1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        imageUri = getIntent().getExtras().getParcelable(IMAGE_URI);

        mCropImageView.setImageUriAsync(imageUri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        Intent intent = new Intent();

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        result.getBitmap().compress(Bitmap.CompressFormat.JPEG, 50, bs);

        intent.putExtra(IMAGE_CROPPED, bs.toByteArray());

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == txtDone) {
            mCropImageView.getCroppedImageAsync();
        }

        if (view == txtCancel) {
            showDialogConfirm();
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error != null) {
            Logger.e("Crop Failed to load image for cropping: ", error.getMessage());
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOkClick() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        showDialogConfirm();
    }

    private void showDialogConfirm() {
        ConfirmCropImageDialog confirmCropImageDialog = new ConfirmCropImageDialog();
        confirmCropImageDialog.show(getFragmentManager(), "ConfirmCropImageDialog");
    }
}
