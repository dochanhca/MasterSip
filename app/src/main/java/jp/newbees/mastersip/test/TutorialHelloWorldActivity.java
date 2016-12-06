package jp.newbees.mastersip.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.linphone.core.LinphoneCoreException;
import org.linphone.core.tutorials.TutorialHelloWorld;
import org.linphone.core.tutorials.TutorialNotifier;
import org.linphone.mediastream.Log;

import jp.newbees.mastersip.R;

/**
 * Created by vietbq on 12/6/16.
 */

public class TutorialHelloWorldActivity extends Activity {
    private static final String defaultSipAddress = "sip:";
    private TextView sipAddressWidget;
    private TutorialHelloWorld tutorial;
    private Handler mHandler =  new Handler() ;
    private Button buttonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello_world);
        sipAddressWidget = (TextView) findViewById(R.id.AddressId);
        sipAddressWidget.setText(defaultSipAddress);

        // Output text to the outputText widget
        final TextView outputText = (TextView) findViewById(R.id.OutputText);
        final TutorialNotifier notifier = new AndroidTutorialNotifier(mHandler, outputText);


        // Create HelloWorld object
        tutorial = new TutorialHelloWorld(notifier);



        // Assign call action to call button
        buttonCall = (Button) findViewById(R.id.CallButton);
        buttonCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TutorialLaunchingThread thread = new TutorialLaunchingThread();
                buttonCall.setEnabled(false);
                thread.start();
            }
        });

        // Assign stop action to stop button
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
                tutorial.launchTutorial(sipAddressWidget.getText().toString());
                mHandler.post(new Runnable() {
                    public void run() {
                        buttonCall.setEnabled(true);
                    }
                });
            } catch (LinphoneCoreException e) {
                Log.e(e);
            }
        }
    }
}
