package jp.newbees.mastersip.ui.mailbackup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.presenter.mailbackup.ChangeEmailBackupPresenter;
import jp.newbees.mastersip.ui.BaseFragment;

/**
 * Created by thangit14 on 2/14/17.
 */
public class ChangeEmailBackupFragment extends BaseFragment{

    private static final int CONFIRM_CHECK_CODE_DIALOG = 1;

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;
    @BindView(R.id.edt_password)
    HiraginoEditText edtPassword;
    @BindView(R.id.edt_re_password)
    HiraginoEditText edtRePassword;
    @BindView(R.id.btn_register)
    Button btnRegister;

    private ChangeEmailBackupPresenter presenter;

    @Override
    protected int layoutId() {
        return R.layout.fragment_change_email_backup;

    }

    @Override
    protected void init(View mRoot, Bundle savedInstanceState) {

    }
}
