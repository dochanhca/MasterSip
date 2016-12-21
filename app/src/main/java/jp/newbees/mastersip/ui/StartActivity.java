package jp.newbees.mastersip.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.andexert.library.RippleView;

import java.util.ArrayList;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.adapter.TutorialPagerAdapter;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.ui.BaseActivity;
import jp.newbees.mastersip.ui.auth.LoginActivity;
import jp.newbees.mastersip.ui.auth.RegisterDateOfBirthActivity;
import jp.newbees.mastersip.ui.top.TopActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class StartActivity extends BaseActivity implements View.OnClickListener {

    public static final String IS_REGISTERED = "IS_REGISTERED";

    private Button btnRegister;
    private Button btnLogin;
    private ImageView imgFbLogin;

    private ViewPager pagerTutorial;
    private TutorialPagerAdapter tutorialPagerAdapter;

    @Override
    protected int layoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (checkUserLogin()) {
            startTopScreenWithNewTask();
        } else {
            handleRegisterException();
        }

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin = (Button) findViewById(R.id.btn_login);
        imgFbLogin = (ImageView) findViewById(R.id.img_fb_login);
        pagerTutorial = (ViewPager) findViewById(R.id.pager_tutorial);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        imgFbLogin.setOnClickListener(this);
    }

    @Override
    protected void initVariables(Bundle savedInstanceState) {

        tutorialPagerAdapter = new TutorialPagerAdapter(getApplicationContext(), getDrawableIds());
        pagerTutorial.setAdapter(tutorialPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                goRegisterDOBActivity();
                break;
            case R.id.btn_login:
                goLoginActivity();
                break;
            case R.id.img_fb_login:
                implementFbLogin();
                break;
            default:
                break;
        }
    }

    private void implementFbLogin() {
//        skip
    }

    private void goLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void goRegisterDOBActivity() {

        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        startActivity(intent);
    }

    private ArrayList<Integer> getDrawableIds() {
        ArrayList<Integer> drawableIds = new ArrayList<>();
        drawableIds.add(R.drawable.master_sip_logo);
        drawableIds.add(R.drawable.slide_tutorial_page_2);
        drawableIds.add(R.drawable.slide_tutorial_page_3);
        drawableIds.add(R.drawable.slide_tutorial_page_4);
        return drawableIds;
    }
    /**
     * User registered
     * if gender = Male redirect to Register Profile Screen
     * else redirect to Tip Page Screen
     */
    private void handleRegisterException() {
        if (getUserItem() == null) {
            return;
        }

        Intent intent = new Intent(getApplicationContext(), RegisterDateOfBirthActivity.class);
        intent.putExtra(IS_REGISTERED, true);
        startActivity(intent);
    }

    private void startTopScreenWithNewTask() {
        Intent intent = new Intent(getApplicationContext(), TopActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}
