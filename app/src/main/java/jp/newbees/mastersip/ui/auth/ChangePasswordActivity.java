package jp.newbees.mastersip.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.presenter.auth.ChangePassPresenter;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 2/15/17.
 */

public class ChangePasswordActivity extends RegisterBaseActivity implements ChangePassPresenter.ChangePassView {

    private static final String EMAIL = "EMAIL";
    @BindView(R.id.edt_code)
    HiraginoEditText edtCode;
    @BindView(R.id.edt_password)
    HiraginoEditText edtPassword;

    private ChangePassPresenter changePassPresenter;
    private String email;

    @Override
    protected int layoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.password_setting));
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        changePassPresenter = new ChangePassPresenter(getApplicationContext(), this);
        email = getIntent().getStringExtra(EMAIL);
    }

    @OnClick(R.id.btn_registration)
    public void onClick() {
        String pass = edtPassword.getText().toString().trim();
        String code = edtCode.getText().toString().trim();
        if (checkDataValid(pass, code)) {
            showLoading();
            changePassPresenter.changePass(email, pass, code);
        }
    }

    @Override
    public void didChangePass() {
        disMissLoading();
        startTopScreenWithNewTask();
    }

    @Override
    public void didChangePassError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    private boolean checkDataValid(String pass, String code) {
        boolean isDataValid = true;

        if (!Utils.isValidPassword(pass)) {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_password_format), Toast.LENGTH_SHORT).show();
            isDataValid = false;
        } else if (code.length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.err_invalid_code), Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }
        return isDataValid;
    }


    /**
     * @param activity
     * @param email
     */
    public static void startActivity(Activity activity, String email) {
        Intent intent = new Intent(activity, ChangePasswordActivity.class);
        intent.putExtra(EMAIL, email);
        activity.startActivity(intent);
    }
}
