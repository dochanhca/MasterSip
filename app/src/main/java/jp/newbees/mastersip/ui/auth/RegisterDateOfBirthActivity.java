package jp.newbees.mastersip.ui.auth;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.utils.Enum;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterDateOfBirthActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Button btnTerm;
    private Button btnPolicy;
    private Button btnRegisterDOB;

    private TextView txtGender;
    private TextView txtDOB;

    private int gender = -1;

    @Override
    protected int layoutId() {
        return R.layout.activity_register_dob;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        btnTerm = (Button) findViewById(R.id.btn_term);
        btnPolicy = (Button) findViewById(R.id.btn_policy);
        btnRegisterDOB = (Button) findViewById(R.id.btn_register_dob);
        txtGender = (TextView) findViewById(R.id.txt_gender);
        txtDOB = (TextView) findViewById(R.id.txt_dob);

        btnTerm.setOnClickListener(this);
        btnPolicy.setOnClickListener(this);
        btnRegisterDOB.setOnClickListener(this);
        txtGender.setOnClickListener(this);
        txtDOB.setOnClickListener(this);

        initHeader(getString(R.string.register_dob_activity));
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_term:
                goTermActivity();
                break;
            case R.id.btn_policy:
                goPolicyActivity();
                break;
            case R.id.btn_register_dob:
                registerDOB();
                break;
            case R.id.txt_gender:
                openGenderPicker();
                break;
            case R.id.txt_dob:
                openDatePicker();
                break;
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


    }

    private void registerDOB() {

        if (gender == Enum.Gender.MALE.getValue()) {
            Intent intent = new Intent(getApplicationContext(), RegisterInfoActivity.class);
            startActivity(intent);
        } else if (gender == Enum.Gender.FEMALE.getValue()) {
            Intent intent = new Intent(getApplicationContext(), TipPageActivity.class);
            startActivity(intent);
        }

    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                this, year, month, day);
        datePickerDialog.show();
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

        numberPicker.setDisplayedValues(new String[]{"Male", "Female"});
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
}
