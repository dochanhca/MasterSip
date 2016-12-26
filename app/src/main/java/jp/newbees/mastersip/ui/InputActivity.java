package jp.newbees.mastersip.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.utils.Utils;

/**
 * Created by ducpv on 12/15/16.
 */

public class InputActivity extends BaseActivity {

    public static final String TITLE = "TITLE";
    public static final String INPUT_DATA = "INPUT_DATA";
    public static final String TEXT_CONTENT = "TEXT CONTENT";
    public static final int INPUT_ACTIVITY_REQUEST_CODE = 11;


    private EditText edtData;
    private String title;
    private String textContent;
    private ViewGroup rootView;

    private boolean isKeyboardShowing = false;

    @Override
    protected int layoutId() {
        return R.layout.activity_input;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        edtData = (EditText) findViewById(R.id.edt_data);

        title = getIntent().getStringExtra(TITLE);
        textContent = getIntent().getStringExtra(TEXT_CONTENT);

        txtActionBarTitle = (TextView) findViewById(R.id.txt_action_bar_title);
        imgBack = (ImageView) findViewById(R.id.img_back);
        rootView = (ViewGroup) findViewById(R.id.root_view);

        txtActionBarTitle.setText(title);
        edtData.setText(textContent);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftwareKeyboard();
                putDataBack();
            }
        });
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

    }

    @Override
    public void onBackPressed() {
        putDataBack();
    }

    private void putDataBack() {
        String inputData = edtData.getText().toString().trim();
        Intent intent = new Intent();
        intent.putExtra(INPUT_DATA, inputData);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void hideSoftwareKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
