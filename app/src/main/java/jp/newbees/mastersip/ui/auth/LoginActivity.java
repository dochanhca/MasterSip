package jp.newbees.mastersip.ui.auth;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.presenter.auth.LoginPresenter;

/**
 * Created by vietbq on 12/6/16.
 */

public class LoginActivity extends RegisterBaseActivity implements View.OnClickListener, LoginPresenter.LoginView {

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.txt_forgot_pass)
    TextView txtForgotPass;

    private LoginPresenter loginPresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initHeader(getString(R.string.login));

        txtForgotPass.setPaintFlags(txtForgotPass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        loginPresenter = new LoginPresenter(getApplicationContext(), this);
    }

    @OnClick({R.id.btn_login, R.id.txt_forgot_pass})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                doLoginByEmail();
                break;
            case R.id.txt_forgot_pass:
                ForgotPasswordActivity.startActivity(this);
                break;
        }
    }

    private void doLoginByEmail() {
        showLoading();
        loginPresenter.loginByEmail(edtEmail.getText().toString().trim(),
                edtPassword.getText().toString().trim());
    }

    @Override
    public void didLoginVoIP() {
        disMissLoading();
        startTopScreenWithNewTask();
    }

    @Override
    public void didLoginError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }
}
