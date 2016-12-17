package jp.newbees.mastersip.ui.auth;

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
import jp.newbees.mastersip.ui.BaseActivity;

/**
 * Created by vietbq on 12/6/16.
 */

public class StartActivity extends BaseActivity implements View.OnClickListener {

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

/*
    private void testShowDialog() {
        SelectionDialog dialog = new SelectionDialog();
        ArrayList<SelectionItem> selectionItems = new ArrayList<>();
        for (int i= 0;i<30;i++){
            selectionItems.add(new SelectionItem(i,"Item " + i));
        }
        Bundle args = new Bundle();
        args.putParcelableArrayList(SelectionDialog.LIST_SELECTION,selectionItems);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(),SelectionDialog.TAG);
    }
*/
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
}
