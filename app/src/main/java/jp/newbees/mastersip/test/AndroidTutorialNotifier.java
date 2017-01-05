package jp.newbees.mastersip.test;

import android.os.Handler;
import android.widget.TextView;

import org.linphone.core.tutorials.TutorialNotifier;

/**
 * Created by vietbq on 12/6/16.
 */

public class AndroidTutorialNotifier extends TutorialNotifier {
    private Handler mHandler;
    private TextView outputTextView;

    public AndroidTutorialNotifier(Handler mHandler, final TextView outputTextView) {
        this.mHandler = mHandler;
        this.outputTextView = outputTextView;
    }


    @Override
    public void notify(final String s) {
        mHandler.post(new Runnable() {
            public void run() {
                outputTextView.setText(s + "\n" + outputTextView.getText());
            }
        });
    }

    public void registerVoIPOK() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}
