package jp.newbees.mastersip.ui;

import android.content.Context;

import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.presenter.CallMaker;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 3/30/17.
 */

public abstract class BaseCallFragment extends BaseFragment  {
    private  CallMaker callMaker;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallMaker) {
            callMaker = (CallMaker) context;
        }else {
            Logger.e("BaseCallFragment", "Your activity must be implement CallMaker");
        }
    }

    public final void callVideo(UserItem callee,boolean fromProfileDetail) {
        callMaker.callVideo(callee, fromProfileDetail);
    }

    public final void callVoice(UserItem callee,boolean fromProfileDetail) {
        callMaker.callVoice(callee, fromProfileDetail);
    }

    public final void chatWithUser(UserItem userItem) {
        callMaker.chat(userItem);
    }

    public final void gotoProfileDetail(UserItem userItem) {
        callMaker.gotoProfileFromActivity(userItem);
    }

    public final void setShowingProfile(UserItem userItem) {
        callMaker.setShowingProfile(userItem);
    }
}
