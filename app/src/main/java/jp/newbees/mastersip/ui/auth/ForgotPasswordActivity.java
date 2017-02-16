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
import jp.newbees.mastersip.presenter.auth.ResetPassPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 2/15/17.
 */

public class ForgotPasswordActivity extends BaseActivity implements ResetPassPresenter.ResetPassView,
        TextDialog.OnTextDialogClick {

    private static final int REQUEST_CHANGE_PASS = 2;

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;

    ResetPassPresenter resetPassPresenter;

    @Override
    protected int layoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initHeader(getString(R.string.password_resetting));
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {
        resetPassPresenter = new ResetPassPresenter(getApplicationContext(), this);
    }

    @OnClick(R.id.btn_send)
    public void onClick() {
        String email = edtEmail.getText().toString().trim();

        if (!Utils.isValidEmail(email)) {
            Toast.makeText(getApplicationContext(), getString(R.string.content_wrong_email_format), Toast.LENGTH_SHORT).show();
        } else {
            showLoading();
            resetPassPresenter.resetPassword(edtEmail.getText().toString().trim());
        }
    }

    @Override
    public void didResetPass() {
        disMissLoading();
        showDialogRegisterSuccess();
    }

    @Override
    public void didResetPassError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(getApplicationContext(), errorCode, errorMessage);
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        ChangePasswordActivity.startActivityForResult(this, edtEmail.getText().toString().trim(),
                REQUEST_CHANGE_PASS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANGE_PASS && resultCode == RESULT_OK) {
            finish();
        }
    }

    private void showDialogRegisterSuccess() {
        String title = getResources().getString(R.string.title_send_confirm_email_backup);
        String content = getResources().getString(R.string.content_send_confirm_email_backup_error);
        TextDialog.openTextDialog(getSupportFragmentManager(), content, title, "", true);
    }

    /**
     * @param activity
     */
    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ForgotPasswordActivity.class);
        activity.startActivity(intent);
    }
}
