package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.top.TopActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class RegisterDateOfBirthActivity extends BaseActivity implements View.OnClickListener {

    private Button btnTerm;
    private Button btnPolicy;
    private Button btnRegisterDOB;

    private TextView txtGender;

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

        btnTerm.setOnClickListener(this);
        btnPolicy.setOnClickListener(this);
        btnRegisterDOB.setOnClickListener(this);
        txtGender.setOnClickListener(this);
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
            case R.id.btn_login:
                break;
            case R.id.txt_gender:
                openGenderPicker();
        }
    }

    private void openGenderPicker() {
        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(0);
        picker.setMaxValue(1);

        picker.setDisplayedValues(new String[]{"Male", "Female"});
        picker.
    }

    private void goPolicyActivity() {

    }

    private void goTermActivity() {

    }
}
