package jp.newbees.mastersip.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.InputActivity;
import jp.newbees.mastersip.ui.ProfileBaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.Constant;

/**
 * Created by vietbq on 12/6/16.
 */

public class UpdateProfileMaleActivity extends ProfileBaseActivity implements View.OnClickListener,
        SelectionDialog.OnSelectionDialogClick, TextDialog.OnTextDialogPositiveClick {

    private ArrayList<SelectionItem> maleJobItems;

    @Override
    protected int layoutId() {
        return R.layout.activity_update_profile_male;
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        super.initVariables(savedInstanceState);
        maleJobItems = new ArrayList<>();

        String[] maleJobs = getResources().getStringArray(R.array.male_job);
        for (int i = 0; i < maleJobs.length; i++) {
            SelectionItem selectionItem = new SelectionItem(i + Constant.Application.START_MALE_JOB_ID, maleJobs[i]);
            maleJobItems.add(selectionItem);
        }
    }

    @Override
    public void onItemSelected(int position) {
        jobItem = maleJobItems.get(position);
        txtProfession.setText(jobItem.getTitle());
    }

    @Override
    protected void selectJob() {
        if (jobItem == null) {
            jobItem = new SelectionItem();
        }
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                maleJobItems, getString(R.string.profession), jobItem);
    }

    @Override
    protected void handleDataInput(Intent data) {
        String content = data.getStringExtra(InputActivity.INPUT_DATA);
        txtStatusContent.setText(content);
        showTextViewIfHasData(content, imgDividerStatus, txtStatusContent);
    }

    @Override
    protected void updateProfile() {
        updateCommonProfile();

        // Send Request to server
        showLoading();
        updateRegisterProfilePresenter.updateRegisterProfile(userItem,
                mode == MODE_REGISTER ? true : false);
    }

    @Override
    protected void fillProfile() {
        fillCommonProfile();
    }

    @Override
    protected void inputStatus() {
        goToInputDataActivity(getString(R.string.status),
                txtStatusContent.getText().toString());
    }

    public static void startActivityForResult(Fragment fragment, int from, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(),
                UpdateProfileMaleActivity.class);
        intent.putExtra(START_MODE, from);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Activity activity, int from) {
        Intent intent = new Intent(activity,
                UpdateProfileMaleActivity.class);
        intent.putExtra(START_MODE, from);
        activity.startActivity(intent);
    }

}
