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
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.ConstantTest;
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
        calendar.add(Calendar.YEAR, -(ConstantTest.MIN_AGE + 1)); // to get previous year add -MIN_AGE + 1
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
                showDialogSelectGender();
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
        } else if (myAge < ConstantTest.MIN_AGE) {
            showMessageDialog("", getString(R.string.err_age_less_than_18), "");
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

        numberPicker.setMinValue(Enum.Gender.MALE.getValue());
        numberPicker.setMaxValue(Enum.Gender.FEMALE.getValue());
        numberPicker.setValue(Enum.Gender.MALE.getValue());
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
