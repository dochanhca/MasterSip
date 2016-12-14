package jp.newbees.mastersip.ui.dialog;

import android.Manifest;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.File;

import jp.newbees.mastersip.R;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by ducpv on 12/14/16.
 */

public class SelectAvatarDialog extends BaseDialog implements View.OnClickListener {

    public static final int PICK_AVATAR_CAMERA = 1;
    public static final int PICK_AVATAR_GALLERY = 2;
    public static final int CROP_IMAGE = 4;
    private static final int CAMERA_PERMISSION = 10;
    private static final int GALLERY_PERMISSION = 11;
    public static final String AVATAR_NAME = "/avatar.jpg";

    private RelativeLayout layoutTakeAPicture;
    private RelativeLayout layoutSelectPicture;
    private RelativeLayout layoutCancel;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        layoutTakeAPicture = (RelativeLayout) rootView.findViewById(R.id.layout_take_picture);
        layoutSelectPicture = (RelativeLayout) rootView.findViewById(R.id.layout_select_picture);
        layoutCancel = (RelativeLayout) rootView.findViewById(R.id.layout_cancel);

        layoutTakeAPicture.setOnClickListener(this);
        layoutSelectPicture.setOnClickListener(this);
        layoutCancel.setOnClickListener(this);

        hideLayoutActions();

        getDialog().getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(p);
    }

    @Override
    protected int getLayoutDialog() {
        return R.layout.dialog_select_avatar;
    }

    @Override
    public void onClick(View view) {
        if (view == layoutTakeAPicture) {
            checkCameraPermission();
        } else if (view == layoutSelectPicture) {
            checkStoragePermission();
        } else {
            dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                openCamera();
                this.dismiss();
            }
        } else if (requestCode == GALLERY_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                openGallery();
                this.dismiss();
            }
        }

    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                    CAMERA_PERMISSION);
        } else {
            openCamera();
            this.dismiss();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_PERMISSION);
        } else {
            openGallery();
            this.dismiss();
        }
    }

    public static void showDialogSelectAvatar(FragmentActivity context) {
        SelectAvatarDialog selectAvatarDialog = new SelectAvatarDialog();
        selectAvatarDialog.show(context.getFragmentManager(), "SelectAvatarDialog");
    }


    private void openCamera() {
        String path = Environment.getExternalStorageDirectory() + AVATAR_NAME;
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        getActivity().startActivityForResult(takePicture, PICK_AVATAR_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivity() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            getActivity().startActivityForResult(intent, PICK_AVATAR_GALLERY);
        }
    }
}
