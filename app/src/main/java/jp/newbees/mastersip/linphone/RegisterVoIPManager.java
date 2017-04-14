package jp.newbees.mastersip.linphone;

import org.linphone.core.LinphoneCore;


/**
 * Created by ducpv on 1/4/17.
 */
public class RegisterVoIPManager {
    private static RegisterVoIPManager instance = null;
    private boolean registrationPrgress;

    private RegisterVoIPManager() {
    }

    public static RegisterVoIPManager getInstance() {
        if (instance == null) {
            instance = new RegisterVoIPManager();
        }
        return instance;
    }

    public void registrationStateChanged(LinphoneCore.RegistrationState state, LinphoneNotifier notifier) {
        if (state == LinphoneCore.RegistrationState.RegistrationOk) {
            notifier.registerVoIPSuccess(this.registrationPrgress);
            this.registrationPrgress = false;
        }else if(state == LinphoneCore.RegistrationState.RegistrationProgress) {
            this.registrationPrgress = true;
        } else if (state == LinphoneCore.RegistrationState.RegistrationFailed){
            notifier.registerVoIPFailed();
        } else if (state == LinphoneCore.RegistrationState.RegistrationCleared) {
            //nothing
        }
    }
}
