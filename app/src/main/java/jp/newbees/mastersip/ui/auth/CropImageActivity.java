package jp.newbees.mastersip.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;
import com.theartofdev.edmodo.cropper.CropImageView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectImageDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.FileUtils;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by ducpv on 12/14/16.
 */

public class CropImageActivity extends BaseActivity implements CropImageView.OnCropImageCompleteListener,
        View.OnClickListener, CropImageView.OnSetImageUriCompleteListener, TextDialog.OnTextDialogPositiveClick {


    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String IMAGE_CROPPED = "IMAGE_URI";

    private Uri imageUri;

    private CropImageView mCropImageView;
    private TextView txtCancel;
    private TextView txtDone;
    private ProgressWheel prwLoadingImage;

    @Override
    protected int layoutId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        mCropImageView = (CropImageView) findViewById(R.id.crop_image);
        txtCancel = (TextView) findViewById(R.id.txt_cancel);
        txtDone = (TextView) findViewById(R.id.txt_done);
        prwLoadingImage = (ProgressWheel) findViewById(R.id.progress_wheel);

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
        Logger.e("Image Cropped: ", "width: " + result.getBitmap().getWidth()
                + "- height: " + result.getBitmap().getHeight());
        new AsyncTask<Bitmap, Void, String>() {
            @Override
            protected String doInBackground(Bitmap[] params) {
                String fileName = "android_" + System.currentTimeMillis() + ".png";
                final String filePath = FileUtils.saveBitmapToFile(params[0], fileName);
                return filePath;
            }

            @Override
            protected void onPostExecute(String filePath) {
                prwLoadingImage.setVisibility(View.INVISIBLE);
                Intent intent = new Intent();
                intent.putExtra(IMAGE_CROPPED, filePath);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.execute(result.getBitmap());
    }

    @Override
    public void onClick(View view) {
        if (view == txtDone) {
            prwLoadingImage.setVisibility(View.VISIBLE);
            mCropImageView.getCroppedImageAsync();
        }

        if (view == txtCancel) {
            showDialogConfirm();
        }
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        prwLoadingImage.setVisibility(View.INVISIBLE);
        if (error != null) {
            Logger.e("Crop Failed to load image for cropping: ", error.getMessage());
            Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        showDialogConfirm();
    }

    private void showDialogConfirm() {
        TextDialog textDialog = new TextDialog.Builder()
                .build(getString(R.string.mess_confirm_cancel_crop_image));
        textDialog.show(getSupportFragmentManager(), TextDialog.class.getSimpleName());
    }

    /**
     * @param activity
     * @param imageUri
     */
    public static void startActivityForResult(Activity activity, Uri imageUri) {
        Intent intent = new Intent(activity, CropImageActivity.class);
        intent.putExtra(CropImageActivity.IMAGE_URI, imageUri);

        activity.startActivityForResult(intent, SelectImageDialog.CROP_IMAGE);
    }

    /**
     * @param fragment
     * @param imageUri
     */
    public static void startActivityForResult(Fragment fragment, Uri imageUri) {
        Intent intent = new Intent(fragment.getActivity().getApplicationContext(), CropImageActivity.class);
        intent.putExtra(CropImageActivity.IMAGE_URI, imageUri);

        fragment.startActivityForResult(intent, SelectImageDialog.CROP_IMAGE);
    }
}
