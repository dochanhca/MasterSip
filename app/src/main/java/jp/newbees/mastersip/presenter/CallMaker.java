package jp.newbees.mastersip.presenter;

import jp.newbees.mastersip.model.UserItem;

/**
 * Created by vietbq on 4/3/17.
 */

public interface CallMaker {
    void callVideo(UserItem callee, boolean fromProfileDetail);
    void callVoice(UserItem callee, boolean fromProfileDetail);
    void chat(UserItem chatter);
    void gotoProfileFromActivity(UserItem userItem);
    void setShowingProfile(UserItem userItem);
}

