package jp.newbees.mastersip.ui.mailbackup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.model.EmailBackupItem;
import jp.newbees.mastersip.presenter.mailbackup.RegisterEmailBackupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.top.MyMenuContainerFragment;
import jp.newbees.mastersip.utils.ConfigManager;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by thangit14 on 2/14/17.
 */

public class RegisterEmailBackupFragment extends BaseFragment implements RegisterEmailBackupPresenter.RegisterEmailBackupListener,TextDialog.OnTextDialogClick {

    private static final int CONFIRM_CHECK_CODE_DIALOG = 1;

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;
    @BindView(R.id.edt_password)
    HiraginoEditText edtPassword;
    @BindView(R.id.edt_re_password)
    HiraginoEditText edtRePassword;
    @BindView(R.id.btn_register)
    Button btnRegister;

    private RegisterEmailBackupPresenter presenter;

    @Override
    protected int layoutId() {
        return R.layout.fragment_register_email_backup;
    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {
        ButterKnife.bind(this, mRoot);
        setFragmentTitle(getContext().getResources().getString(R.string.title_register_backup_email));
        presenter = new RegisterEmailBackupPresenter(getContext(), this);
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        String message = Utils.validateEmailAndPassword(getContext()
                , edtEmail.getText().toString()
                , edtPassword.getText().toString()
                , edtRePassword.getText().toString());
        if (message.length() == 0) {
            showLoading();
            presenter.registerEmail(getMailBackupItem());
        } else {
            showMessageDialog(message);
        }
    }

    @Override
    public void onRegisterEmailBackupSuccess() {
        disMissLoading();
        Utils.showDialogRegisterSuccess(CONFIRM_CHECK_CODE_DIALOG,this);
    }

    @Override
    public void onRegisterEmailBackupError(int errorCode, String errorMessage) {
        disMissLoading();
        showToastExceptionVolleyError(errorCode, errorMessage);
    }

    @Override
    public void onTextDialogOkClick(int requestCode) {
        if (requestCode == CONFIRM_CHECK_CODE_DIALOG) {
            MyMenuContainerFragment.showCheckCodeFragmentFromRegisterEmailBackUp(getActivity());
        }
    }

    private EmailBackupItem getMailBackupItem() {
        EmailBackupItem item = new EmailBackupItem();
        item.setEmail(edtEmail.getText().toString());
        item.setPass(edtPassword.getText().toString());
        item.setExtension(ConfigManager.getInstance().getCurrentUser().getSipItem().getExtension());
        return item;
    }

    public static RegisterEmailBackupFragment newInstance() {
        Bundle args = new Bundle();
        RegisterEmailBackupFragment fragment = new RegisterEmailBackupFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
