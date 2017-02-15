package jp.newbees.mastersip.ui.mailbackup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.newbees.mastersip.R;
import jp.newbees.mastersip.customviews.HiraginoEditText;
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by thangit14 on 2/13/17.
 */

public class RegisterMailBackupActivity extends BaseActivity {

    @BindView(R.id.edt_email)
    HiraginoEditText edtEmail;
    @BindView(R.id.edt_password)
    HiraginoEditText edtPassword;
    @BindView(R.id.edt_re_password)
    HiraginoEditText edtRePassword;
    @BindView(R.id.btn_register)
    Button btnRegister;

    @Override
    protected int layoutId() {
        return R.layout.fragment_register_email_backup;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, RegisterMailBackupActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.btn_register)
    public void onClick() {

    }
}
