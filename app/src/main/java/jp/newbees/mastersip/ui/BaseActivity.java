package jp.newbees.mastersip.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.utils.SmartLog;

/**
 * Created by vietbq on 12/6/16.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmartLog.logE(TAG, "Create");

        setContentView(layoutId());
        initViews(savedInstanceState);
        initVariables(savedInstanceState);
    }

    protected abstract int layoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initVariables(Bundle savedInstanceState);

    protected void initHeader(String title) {
        TextView txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);

        txtActionBarTitle.setText(title);
    }
}
