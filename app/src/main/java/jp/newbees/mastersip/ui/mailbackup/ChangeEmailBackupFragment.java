package jp.newbees.mastersip.ui.mailbackup;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.mailbackup.ChangeEmailBackupPresenter;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.auth.ForgotPasswordActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.top.MyMenuContainerFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 2/14/17.
 */
public class ChangeEmailBackupFragment extends BaseFragment implements ChangeEmailBackupPresenter.ChangeEmailBackupListener, TextDialog.OnTextDialogPositiveClick {

    private static final int CONFIRM_CHECK_CODE_DIALOG = 1;

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;
    @BindView(R.id.edt_password)
    HiraginoEditText edtPassword;
    @BindView(R.id.edt_re_password)
    HiraginoEditText edtRePassword;
    @BindView(R.id.txt_old_email)
    TextView txtOldEmail;
    @BindView(R.id.edt_old_pass)
    TextView edtOldPass;

    private ChangeEmailBackupPresenter presenter;
    private UserItem currentUser;

    public static ChangeEmailBackupFragment newInstance() {
        ChangeEmailBackupFragment fragment = new ChangeEmailBackupFragment();
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_change_email_backup;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        setFragmentTitle(getResources().getString(R.string.title_change_email_backup_fragment));
        ButterKnife.bind(this, mRoot);
        currentUser = ConfigManager.getInstance().getCurrentUser();
        txtOldEmail.setText(currentUser.getEmail());
        presenter = new ChangeEmailBackupPresenter(getContext(), this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnBackPressed(new BaseActivity.OnBackPressed() {
            @Override
            public void onBackPressed() {
                presenter.backToMyMenuFragment(getFragmentManager());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        setOnBackPressed(null);
    }

    @Override
    public void onChangeEmailBackupSuccess() {
        disMissLoading();
        Utils.showDialogRegisterSuccess(CONFIRM_CHECK_CODE_DIALOG, this);
    }

    @Override
    public void onChangeEmailBackupError(int errorCode, String errorMessage) {
        disMissLoading();
        if (errorCode == Constant.Error.INVALID_PASSWORD) {
            showMessageDialog(getString(R.string.err_invalid_pass));
        } else {
            showToastExceptionVolleyError(errorCode, errorMessage);
        }
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == CONFIRM_CHECK_CODE_DIALOG) {
            MyMenuContainerFragment.showCheckCodeFragmentFromChangeEmailBackUp(getActivity(),
                    edtEmail.getText().toString().trim());
        }
    }

    @Override
    protected void onImageBackPressed() {
        presenter.backToMyMenuFragment(getFragmentManager());
    }

    @OnClick({R.id.btn_change,R.id.txt_forgot_pass})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_change) {
            handleChangeEmailClick();

        } else if (view.getId() == R.id.txt_forgot_pass) {
            ForgotPasswordActivity.startActivity(getActivity());
        }
    }

    private void handleChangeEmailClick() {
        String message = presenter.validateParam(edtOldPass, edtEmail, edtPassword, edtRePassword);
        if (message.length() == 0) {
            showLoading();
            presenter.changeEmail(getEmailBackupItem());
        } else {
            showMessageDialog(message);
        }
    }

    private EmailBackupItem getEmailBackupItem() {
        EmailBackupItem item = new EmailBackupItem();
        item.setEmail(edtEmail.getText().toString());
        item.setPass(edtPassword.getText().toString());
        item.setExtension(currentUser.getSipItem().getExtension());
        item.setOldEmail(txtOldEmail.getText().toString());
        item.setOldPass(edtOldPass.getText().toString());
        return item;
    }
}
