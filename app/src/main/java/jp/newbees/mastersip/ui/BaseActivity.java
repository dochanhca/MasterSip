package jp.newbees.mastersip.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.application.MyContextWrapper;
import jp.newbees.mastersip.ui.dialog.MessageDialog;
import jp.newbees.mastersip.utils.ConstantTest;
import jp.newbees.mastersip.utils.SmartLog;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private MessageDialog messageDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, ConstantTest.DEFAULT_LANGUAGE));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmartLog.logE(TAG, "Create");

        setContentView(layoutId());
        initViews(savedInstanceState);
        initVariables(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected abstract int layoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initVariables(Bundle savedInstanceState);

    protected void initHeader(String title) {
        TextView txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);
        ImageView imgBack = (ImageView) findViewById(R.id.img_back);

        txtActionBarTitle.setText(title);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    protected void showMessageDialog(String title, String content, String note) {
        if (null == messageDialog) {
            messageDialog = new MessageDialog();
        }

        if (messageDialog.getDialog() != null && messageDialog.getDialog().isShowing()) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString(MessageDialog.MESSAGE_DIALOG_TITLE, title);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_CONTENT, content);
        bundle.putString(MessageDialog.MESSAGE_DIALOG_NOTE, note);

        messageDialog.setArguments(bundle);
        messageDialog.show(getFragmentManager(), "MessageDialog");
    }
}

