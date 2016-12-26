package jp.newbees.mastersip.ui.auth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.auth.RegisterPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.StartActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterDateOfBirthActivity extends BaseActivity implements View.OnClickListener, RegisterPresenter.RegisterView {

    private Button btnTerm;
    private Button btnPolicy;
    private ImageView imgAccepPolicy;
    private TextView txtGender;
    private TextView txtDOB;
    private ViewGroup layoutDOB;
    private ViewGroup layoutGender;

    private String[] genders;
    private String mDOB = "";
    private String dateSendToServer = "";
    private int myAge = 0;
    private Date defaultDate;
    private Date currentDate;
    private Calendar calendar;

    private int gender = -1;

    private RegisterPresenter registerPresenter;

    private boolean isRegistered;

    @Override
    protected int layoutId() {
        return R.layout.activity_register_dob;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {


        btnTerm = (Button) findViewById(R.id.btn_term_of_services);
        btnPolicy = (Button) findViewById(R.id.btn_policy);
        imgAccepPolicy = (ImageView) findViewById(R.id.img_accep_policy);
        txtGender = (TextView) findViewById(R.id.txt_gender);
        txtDOB = (TextView) findViewById(R.id.txt_dob);
        layoutDOB = (ViewGroup) findViewById(R.id.layout_dob);
        layoutGender = (ViewGroup) findViewById(R.id.layout_gender);

        btnTerm.setOnClickListener(this);
        btnPolicy.setOnClickListener(this);
        imgAccepPolicy.setOnClickListener(this);
        layoutDOB.setOnClickListener(this);
        layoutGender.setOnClickListener(this);

        initHeader(getString(R.string.register_dob_title));

    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        genders = getResources().getStringArray(R.array.array_gender);
        registerPresenter = new RegisterPresenter(this.getApplicationContext(), this);

        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -(Constant.Application.MIN_AGE + 1)); // to get previous year add -MIN_AGE + 1
        defaultDate = calendar.getTime();

        handleRegisterException();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_term_of_services:
                goTermActivity();
                break;
            case R.id.btn_policy:
                goPolicyActivity();
                break;
            case R.id.img_accep_policy:
                registerDOB();
                break;
            case R.id.layout_gender:
                showDialogSelectGender();
                break;
            case R.id.layout_dob:
                openDialogDatePicker();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getUserItem() != null) {
            fillData(getUserItem());
        }
    }

    private void registerDOB() {
        if (!isDataValid())
            return;
        UserItem userItem = new UserItem();

        userItem.setDateOfBirth(dateSendToServer);
        userItem.setGender((gender == 0) ? UserItem.MALE : UserItem.FEMALE);

        showLoading();
        registerPresenter.registerUser(userItem);
    }

    private boolean isDataValid() {
        boolean isDataValid;
        if (txtDOB.getText().length() < 1) {
            showMessageDialog("", getString(R.string.err_dob_empty), "", false);
            isDataValid = false;
        } else if (txtGender.getText().length() < 1) {
            showMessageDialog("", getString(R.string.err_gender_empty), "", false);
            isDataValid = false;
        } else if (myAge < Constant.Application.MIN_AGE) {
            showMessageDialog("", getString(R.string.err_age_less_than_18), "", false);
            isDataValid = false;
        } else {
            isDataValid = true;
        }

        return isDataValid;
    }

    private void goPolicyActivity() {
        Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
        startActivity(intent);
    }

    private void goTermActivity() {
        Intent intent = new Intent(getApplicationContext(), TermsOfServiceActivity.class);
        startActivity(intent);
    }

    private void showDialogSelectGender() {

        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_gender_picker, null);
        alerBuilder.setView(dialogView);

        final MaterialNumberPicker numberPicker = (MaterialNumberPicker) dialogView.findViewById(R.id.picker_gender);
        Button positiveButton = (Button) dialogView.findViewById(R.id.btn_ok);
        Button negativeButton = (Button) dialogView.findViewById(R.id.btn_cancel);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(1);
        numberPicker.setValue(0);
        numberPicker.setDisplayedValues(genders);

        final AlertDialog alertDialog = alerBuilder.create();
        alertDialog.show();

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = numberPicker.getValue();
                txtGender.setText(genders[gender]);
                alertDialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void openDialogDatePicker() {
        if (!mDOB.equalsIgnoreCase("")) {
            try {
                defaultDate = DateTimeUtils.JAPAN_DATE_FORMAT.parse(mDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(onDateSelected)
                .setInitialDate(defaultDate)
                .setMaxDate(currentDate)
                .build()
                .show();
    }

    private SlideDateTimeListener onDateSelected = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            mDOB = DateTimeUtils.JAPAN_DATE_FORMAT.format(date);
            dateSendToServer = DateTimeUtils.ENGLISH_DATE_FORMAT.format(date);
            txtDOB.setText(mDOB);

            myAge = DateTimeUtils.subtractDateToYear(date, currentDate);
        }
    };

    /**
     * User registered
     * if gender = Male redirect to Register Profile Screen
     * else redirect to Tip Page Screen
     */
    private void handleRegisterException() {
        isRegistered = getIntent().getBooleanExtra(StartActivity.IS_REGISTERED, false);
        if (!isRegistered) {
            return;
        }

        UserItem userItem = getUserItem();

        if (userItem.getGender() == UserItem.FEMALE) {
            Intent intent = new Intent(getApplicationContext(), TipPageActivity.class);
            startActivity(intent);
        } else {
            // set DoB
            fillData(userItem);
        }
    }

    private void fillData(UserItem userItem) {
        gender = (userItem.getGender() == UserItem.MALE) ? 0 : 1;
        txtGender.setText(genders[gender]);
        try {
            defaultDate = DateTimeUtils.ENGLISH_DATE_FORMAT.parse(userItem.getDateOfBirth());
            mDOB = DateTimeUtils.JAPAN_DATE_FORMAT.format(defaultDate);
            dateSendToServer = DateTimeUtils.ENGLISH_DATE_FORMAT.format(defaultDate);
            txtDOB.setText(mDOB);

            myAge = DateTimeUtils.subtractDateToYear(defaultDate, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void gotoRegisterProfileActivity(UserItem userItem) {
        if (userItem.getGender() == UserItem.MALE) {
            Intent intent = new Intent(getApplicationContext(), RegisterProfileMaleActivity.class);
            startActivity(intent);
        } else if (userItem.getGender() == UserItem.FEMALE) {
            Intent intent = new Intent(getApplicationContext(), TipPageActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRegistered(UserItem userItem) {
        disMissLoading();
        gotoRegisterProfileActivity(userItem);

    }

    @Override
    public void onRegisterFailure(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }
}
