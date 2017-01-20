package jp.newbees.mastersip.ui.dialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
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
    public static final int CROP_IMAGE = 3;
    public static final String IS_SHOW_BUTTON_DELETE_IMAGE = "IS_SHOW_BUTTON_DELETE_IMAGE";
    private static final int CAMERA_PERMISSION = 10;
    private static final int GALLERY_PERMISSION = 11;
    public static final String AVATAR_NAME = "/avatar.jpg";
    private static final String CALL_FROM_ACTIVITY = "CALL_FROM_ACTIVITY";

    private RelativeLayout layoutTakeAPicture;
    private RelativeLayout layoutSelectPicture;
    private RelativeLayout layoutDeletePicture;
    private RelativeLayout layoutCancel;

    private boolean isShowButtonDeleteImage;
    private boolean showFromActivity;

    public interface OnSelectAvatarDiaLogClick {
         void onDeleteImageClick();
    }

    private OnSelectAvatarDiaLogClick onSelectAvatarDiaLogClick;

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        layoutTakeAPicture = (RelativeLayout) rootView.findViewById(R.id.layout_take_picture);
        layoutSelectPicture = (RelativeLayout) rootView.findViewById(R.id.layout_select_picture);
        layoutCancel = (RelativeLayout) rootView.findViewById(R.id.layout_cancel);
        layoutDeletePicture = (RelativeLayout) rootView.findViewById(R.id.layout_delete_picture);

        layoutTakeAPicture.setOnClickListener(this);
        layoutSelectPicture.setOnClickListener(this);
        layoutCancel.setOnClickListener(this);
        layoutDeletePicture.setOnClickListener(this);

        isShowButtonDeleteImage = getArguments().getBoolean(IS_SHOW_BUTTON_DELETE_IMAGE, false);
        showFromActivity = getArguments().getBoolean(CALL_FROM_ACTIVITY, true);

        if (isShowButtonDeleteImage) {
            layoutDeletePicture.setVisibility(View.VISIBLE);
        }

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
        } else if (view == layoutDeletePicture) {
            this.onSelectAvatarDiaLogClick.onDeleteImageClick();
            dismiss();
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
                this.dismissAllowingStateLoss();
                openCamera();

            }
        } else if (requestCode == GALLERY_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.dismissAllowingStateLoss();
                openGallery();
            }
        }
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getTargetFragment() != null) {
            try {
                this.onSelectAvatarDiaLogClick = (SelectAvatarDialog.OnSelectAvatarDiaLogClick) getTargetFragment();
            } catch (ClassCastException e) {
                throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getTargetFragment() == null) {
            try {
                this.onSelectAvatarDiaLogClick = (OnSelectAvatarDiaLogClick) context;
            } catch (ClassCastException e) {
                //
            }
        }
    }

    /**
     * Check use camera and write external storage permission real time
     */
    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            requestPermissions(new String[]{Manifest.permission.CAMERA
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION);
        } else {
            openCamera();
            this.dismiss();
        }
    }



    /**
     * check use read external storage permission real time
     */
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

    public static void showDialogSelectAvatar(FragmentActivity context, boolean isShowButtonDeleteImage) {
        SelectAvatarDialog selectAvatarDialog = new SelectAvatarDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SHOW_BUTTON_DELETE_IMAGE, isShowButtonDeleteImage);
        bundle.putBoolean(CALL_FROM_ACTIVITY, true);
        selectAvatarDialog.setArguments(bundle);
        selectAvatarDialog.show(context.getSupportFragmentManager(), "SelectAvatarDialog");
    }

    public static void showDialogSelectAvatar(Fragment fragment, int requestCode,
                                                       FragmentManager fragmentManager, boolean isShowButtonDeleteImage) {
        SelectAvatarDialog selectionDialog = new SelectAvatarDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_SHOW_BUTTON_DELETE_IMAGE, isShowButtonDeleteImage);
        bundle.putBoolean(CALL_FROM_ACTIVITY, false);
        selectionDialog.setArguments(bundle);
        selectionDialog.setTargetFragment(fragment, requestCode);
        selectionDialog.show(fragmentManager, "SelectAvatarDialog");
    }


    private void openCameraFromActivity(Intent takePicture) {
        getActivity().startActivityForResult(takePicture, PICK_AVATAR_CAMERA);
    }

    private void openCameraFromFragment(Intent takePicture) {
        getTargetFragment().startActivityForResult(takePicture, PICK_AVATAR_CAMERA);
    }


    private void openCamera() {
        String path = Environment.getExternalStorageDirectory() + AVATAR_NAME;
        File file = new File(path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        if (showFromActivity) {
            openCameraFromActivity(takePicture);
        }else {
            openCameraFromFragment(takePicture);
        }
    }

    private void openGallery() {
        if (showFromActivity) {
            openGalleryFromActivity();
        }else {
            openGalleryFromFragment();
        }
    }

    private void openGalleryFromActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            getActivity().startActivityForResult(intent, PICK_AVATAR_GALLERY);
        }
    }

    private void openGalleryFromFragment() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            getTargetFragment().startActivityForResult(intent, PICK_AVATAR_GALLERY);
        }
    }
}
