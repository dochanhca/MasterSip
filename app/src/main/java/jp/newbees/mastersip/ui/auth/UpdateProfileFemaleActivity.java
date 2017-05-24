package jp.newbees.mastersip.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoTextView;
import jp.newbees.mastersip.model.SelectionItem;
import jp.newbees.mastersip.ui.InputActivity;
import jp.newbees.mastersip.ui.ProfileBaseActivity;
import jp.newbees.mastersip.ui.dialog.SelectionDialog;

/**
 * Created by ducpv on 12/13/16.
 */

public class UpdateProfileFemaleActivity extends ProfileBaseActivity implements View.OnClickListener,
        SelectionDialog.OnSelectionDialogClick {

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
    @BindView(R.id.img_divider_type_of_men)
    ImageView imgDividerTypeOfMen;
    @BindView(R.id.img_divider_charm_point)
    ImageView imgDividerCharmPoint;

    private ArrayList<SelectionItem> femaleJobItems;
    private ArrayList<SelectionItem> typeItems;
    private ArrayList<SelectionItem> availableTimeItems;
    ;
    private SelectionItem typeItem;
    private SelectionItem availableTimeItem;

    private InputDataType inputDataType;
    private SelectDataType selectDataType;


    enum InputDataType {
        TYPE_OF_MEN, CHARM_POINT, STATUS
    }

    enum SelectDataType {
        JOB, TYPE, AVAILABLE_TIME
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_update_profile_female;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        super.initViews(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        super.initVariables(savedInstanceState);
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

    @OnClick({R.id.layout_type,
            R.id.layout_type_of_men, R.id.layout_charm_point, R.id.layout_available_time})
    public void onClickForMale(View view) {
        switch (view.getId()) {
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
            default:
                break;
        }
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

    @Override
    protected void updateProfile() {
        updateCommonProfile();

        if (typeItem != null) {
            userItem.setTypeGirl(typeItem);
        }

        String typeOfMen = txtTypeOfMenContent.getText().toString();
        userItem.setTypeBoy(typeOfMen.length() > 0 ? typeOfMen : "");

        String charmPoint = txtCharmPointContent.getText().toString();
        userItem.setCharmingPoint(charmPoint.length() > 0 ? charmPoint : "");

        if (availableTimeItem != null) {
            userItem.setAvailableTimeItem(availableTimeItem);
        }

        // Send Request to server
        showLoading();
        updateRegisterProfilePresenter.updateRegisterProfile(userItem,
                mode == MODE_REGISTER ? true : false);
    }

    @Override
    protected void selectJob() {
        selectDataType = SelectDataType.JOB;
        if (jobItem == null) {
            jobItem = new SelectionItem();
        }
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                femaleJobItems, getString(R.string.profession),"", jobItem);
    }

    @Override
    protected void handleDataInput(Intent data) {
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

    @Override
    protected void fillProfile() {
        fillCommonProfile();

        availableTimeItem = userItem.getAvailableTimeItem();
        txtAvaiableTime.setText(availableTimeItem.getTitle());

        typeItem = userItem.getTypeGirl();
        txtType.setText(typeItem.getTitle());

        txtTypeOfMenContent.setText(userItem.getTypeBoy());
        showTextViewIfHasData(userItem.getTypeBoy(), imgDividerTypeOfMen, txtTypeOfMenContent);

        txtCharmPointContent.setText(userItem.getCharmingPoint());
        showTextViewIfHasData(userItem.getCharmingPoint(), imgDividerCharmPoint, txtCharmPointContent);
    }

    @Override
    protected void inputStatus() {
        inputDataType = InputDataType.STATUS;
        goToInputDataActivity(getString(R.string.status),
                txtStatusContent.getText().toString());
    }

    private void selectAvailableTime() {
        selectDataType = SelectDataType.AVAILABLE_TIME;
        if (availableTimeItem == null) {
            availableTimeItem = new SelectionItem();
        }
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                availableTimeItems, getString(R.string.available_time),"", availableTimeItem);
    }

    private void inputCharmPoint() {
        inputDataType = InputDataType.CHARM_POINT;

        this.goToInputDataActivity(getString(R.string.charm_point), txtCharmPointContent.getText().toString());
    }

    private void inputTypeOfMen() {
        inputDataType = InputDataType.TYPE_OF_MEN;
        this.goToInputDataActivity(getString(R.string.type_of_men), txtTypeOfMenContent.getText().toString());
    }

    private void selectType() {
        selectDataType = SelectDataType.TYPE;
        if (typeItem == null) {
            typeItem = new SelectionItem();
        }
        SelectionDialog.openSelectionDialogFromActivity(getSupportFragmentManager(),
                typeItems, getString(R.string.type),"", typeItem);
    }

    public static void startActivityForResult(Fragment fragment, int from, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(),
                UpdateProfileFemaleActivity.class);
        intent.putExtra(START_MODE, from);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Activity activity, int from) {
        Intent intent = new Intent(activity,
                UpdateProfileFemaleActivity.class);
        intent.putExtra(START_MODE, from);
        activity.startActivity(intent);
    }
}
