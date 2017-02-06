package jp.newbees.mastersip.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.dialog.TextDialog;
import jp.newbees.mastersip.ui.top.TopActivity;

/**
 * Created by ducpv on 1/4/17.
 */

public abstract class RegisterBaseActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    protected void confirmDeleteAvatar() {
        String confirmDeleteAvatar = getString(R.string.confirm_delete_avatar);
        TextDialog.openTextDialog(getSupportFragmentManager(),
                confirmDeleteAvatar, "", "", false);
    }
}
