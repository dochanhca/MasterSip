package jp.newbees.mastersip.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;

import org.linphone.core.LinphoneCoreException;
import org.linphone.core.tutorials.TutorialNotifier;
import org.linphone.mediastream.Log;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.RegisterTask;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 12/6/16.
 */

public class TutorialRegistrationActivity extends TutorialHelloWorldActivity {
    private static final String defaultSipAddress = "sip:";
    private static final String defaultSipPassword = "";
    private TextView sipAddressWidget;
    private TextView sipPasswordWidget;
    private LinphoneHandler tutorial;
    private Button buttonCall;
    private Handler mHandler =  new Handler();
    private TextView outputText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_world);
        sipAddressWidget = (TextView) findViewById(R.id.AddressId);
        sipAddressWidget.setText(defaultSipAddress);
        sipPasswordWidget = (TextView) findViewById(R.id.Password);
        sipPasswordWidget.setVisibility(TextView.VISIBLE);
        sipPasswordWidget.setText(defaultSipPassword);

        // Output text to the outputText widget
        outputText = (TextView) findViewById(R.id.OutputText);
        final TutorialNotifier notifier = new AndroidTutorialNotifier(mHandler, outputText);


        // Create Tutorial object
        tutorial = new LinphoneHandler(notifier,this);



        // Assign call action to call button
        buttonCall = (Button) findViewById(R.id.CallButton);
        buttonCall.setText("Register");
        buttonCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UserItem userItem = new UserItem();
                userItem.setDateOfBirth("1988-10-28");
                userItem.setGender(UserItem.MALE);
                RegisterTask registerTask = new RegisterTask(getApplicationContext(),userItem);
                registerTask.request(new Response.Listener<UserItem>() {
                    @Override
                    public void onResponse(UserItem response) {
                        Logger.d("TutorialRegistrationActivity","request OK");
                    }
                }, new BaseTask.ErrorListener() {
                    @Override
                    public void onError(int errorCode, String errorMessage) {
                        Logger.e("TutorialRegistrationActivity",errorMessage);
                    }
                });
//                TutorialLaunchingThread thread = new TutorialLaunchingThread();
//                buttonCall.setEnabled(false);
//                thread.start();
            }
        });


        Button buttonStop = (Button) findViewById(R.id.ButtonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tutorial.stopMainLoop();
            }
        });
    }


    private class TutorialLaunchingThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                tutorial.launchTutorial(
                        sipAddressWidget.getText().toString(),
                        sipPasswordWidget.getText().toString());
            } catch (LinphoneCoreException e) {
                Log.e(e);
                outputText.setText(e.getMessage() +"\n"+outputText.getText());
            }
        }
    }
}
