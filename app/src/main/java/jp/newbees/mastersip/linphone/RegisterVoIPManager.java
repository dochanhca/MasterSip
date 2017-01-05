package jp.newbees.mastersip.linphone;

import org.linphone.core.LinphoneCore;


/**
 * Created by ducpv on 1/4/17.
 */
public class RegisterVoIPManager {
    private static RegisterVoIPManager instance = null;

    public static RegisterVoIPManager getInstance() {
        if (instance == null) {
            instance = new RegisterVoIPManager();
        }
        return instance;
    }

    private RegisterVoIPManager() {
    }

    public void registrationStateChanged(LinphoneCore.RegistrationState state, LinPhoneNotifier notifier) {
        if (state == LinphoneCore.RegistrationState.RegistrationOk) {
            notifier.registerVoIPSuccess();
        } else if (state == LinphoneCore.RegistrationState.RegistrationFailed){
            notifier.registerVoIPFailed();
        } else if (state == LinphoneCore.RegistrationState.RegistrationCleared) {

        }
    }
}
