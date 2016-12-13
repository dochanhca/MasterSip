package jp.newbees.mastersip.ui.auth;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.Enum;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterDateOfBirthActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgTerm;
    private ImageView imgPolicy;
    private ImageView imgAccepPolicy;
    private TextView txtGender;
    private TextView txtDOB;
    private ViewGroup layoutDOB;
    private ViewGroup layoutGender;

    private String[] genders;
    private String mDOB = "";
    private int myAge = 0;
    private Date defaultDate;
    private Date currentDate;
    private Calendar calendar;

    private int gender = -1;

    @Override
    protected int layoutId() {
        return R.layout.activity_register_dob;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        imgTerm = (ImageView) findViewById(R.id.img_term_of_services);
        imgPolicy = (ImageView) findViewById(R.id.img_policy);
        imgAccepPolicy = (ImageView) findViewById(R.id.img_accep_policy);
        txtGender = (TextView) findViewById(R.id.txt_gender);
        txtDOB = (TextView) findViewById(R.id.txt_dob);
        layoutDOB = (ViewGroup) findViewById(R.id.layout_dob);
        layoutGender = (ViewGroup) findViewById(R.id.layout_gender);

        imgTerm.setOnClickListener(this);
        imgPolicy.setOnClickListener(this);
        imgAccepPolicy.setOnClickListener(this);
        layoutDOB.setOnClickListener(this);
        layoutGender.setOnClickListener(this);

        initHeader(getString(R.string.register_dob_title));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        genders = getResources().getStringArray(R.array.array_gender);

        calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        calendar.add(Calendar.YEAR, -(Constant.MIN_AGE + 1)); // to get previous year add -MIN_AGE + 1
        defaultDate = calendar.getTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_term_of_services:
                goTermActivity();
                break;
            case R.id.img_policy:
                goPolicyActivity();
                break;
            case R.id.img_accep_policy:
                registerDOB();
                break;
            case R.id.layout_gender:
                openGenderPicker();
                break;
            case R.id.layout_dob:
                openDialogDatePicker();
                break;
            default:
                break;
        }
    }

    private void registerDOB() {

        if (!isDataValid())
            return;

        if (gender == Enum.Gender.MALE.getValue()) {
            Intent intent = new Intent(getApplicationContext(), RegisterInfoActivity.class);
            startActivity(intent);
        } else if (gender == Enum.Gender.FEMALE.getValue()) {
            Intent intent = new Intent(getApplicationContext(), TipPageActivity.class);
            startActivity(intent);
        }

    }

    private boolean isDataValid() {
        boolean isDataValid;
        if (txtDOB.getText().length() < 1) {
            showMessageDialog("", getString(R.string.err_dob_empty), "");
            isDataValid = false;
        } else if (txtGender.getText().length() < 1) {
            showMessageDialog("", getString(R.string.err_gender_empty), "");
            isDataValid = false;
        } else if (myAge < Constant.MIN_AGE) {
            showMessageDialog("", getString(R.string.err_age_less_than_18), "");
            isDataValid = false;
        } else {
            isDataValid = true;
        }

        return isDataValid;
    }

    private void openGenderPicker() {
        final MaterialNumberPicker numberPicker = new MaterialNumberPicker.Builder(this)
                .minValue(Enum.Gender.MALE.getValue())
                .maxValue(Enum.Gender.FEMALE.getValue())
                .defaultValue(Enum.Gender.MALE.getValue())
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.GRAY)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        numberPicker.setDisplayedValues(genders);
        showDialogSelectGender(numberPicker);
    }

    private void goPolicyActivity() {
        Intent intent = new Intent(getApplicationContext(), PolicyActivity.class);
        startActivity(intent);
    }

    private void goTermActivity() {
        Intent intent = new Intent(getApplicationContext(), TermsOfServiceActivity.class);
        startActivity(intent);
    }

    private void showDialogSelectGender(final MaterialNumberPicker numberPicker) {
        AlertDialog alertDialog;

        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(this);

        alerBuilder
                .setView(numberPicker)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gender = numberPicker.getValue();
                        txtGender.setText(genders[gender]);
                    }
                })
                .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog = alerBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }

    private void openDialogDatePicker() {
        if (!mDOB.equalsIgnoreCase("")) {
            try {
                defaultDate = DateTimeUtils.DATE_FORMAT.parse(mDOB);
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
            mDOB = DateTimeUtils.DATE_FORMAT.format(date);
            txtDOB.setText(mDOB);

            myAge = DateTimeUtils.subtractDateToYear(date, currentDate);
        }
    };
}
