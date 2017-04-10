package jp.newbees.mastersip.presenter.auth;

import android.app.Activity;
import android.content.Context;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import jp.newbees.mastersip.model.ImageItem;
import jp.newbees.mastersip.model.UserItem;
import jp.newbees.mastersip.network.api.BaseTask;
import jp.newbees.mastersip.network.api.LoginFacebookTask;
import jp.newbees.mastersip.network.api.RegisterTask;
import jp.newbees.mastersip.utils.Constant;
import jp.newbees.mastersip.utils.DateTimeUtils;
import jp.newbees.mastersip.utils.FacebookUtils;
import jp.newbees.mastersip.utils.JapaneseCharacter;
import jp.newbees.mastersip.utils.Logger;

import static jp.newbees.mastersip.utils.Constant.Application.MIN_AGE;

/**
 * Created by vietbq on 1/9/17.
 */

public class StartPresenter extends RegisterPresenterBase {
    private StartView startView;

    public interface StartView {
        void didLoginVoIP();

        void didErrorVoIP(String errorMessage);

        void didLoadFacebookFailure(String errorMessage);

        void didLoginFacebookMissingBirthday(UserItem userItem);

        void didLoginFacebookButNotRegisterOnServer(UserItem userItem);

        void didBirthdayIsBelow18();
    }

    public StartPresenter(Context context, StartView startView) {
        super(context);
        this.startView = startView;
    }

    public void loginFacebook(Activity activity
            , CallbackManager callbackManager) {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Logger.e(tag, "Login FB success");
                        handleLoginResult(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Logger.e(tag, "Login FB cancel");
                        startView.didLoadFacebookFailure("User cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        startView.didLoadFacebookFailure(exception.toString());
                        Logger.e(tag, "Login FB error " + exception.getLocalizedMessage());
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_birthday"));
    }


    private void handleLoginResult(LoginResult loginResult) {
        String facebookId = loginResult.getAccessToken().getUserId();
        UserItem userItem = new UserItem();
        userItem.setFacebookId(facebookId);
        String accessToken = loginResult.getAccessToken().getToken();
        this.loadFacebookUserInfo(accessToken, userItem);
    }

    private void loadFacebookUserInfo(final String accessToken, final UserItem userItem) {
        FacebookUtils.getGraphMeRequestAsync(accessToken, new Utility.GraphMeRequestWithCacheCallback() {
            @Override
            public void onSuccess(JSONObject userInfo) {
                handleFacebookUserInfo(userInfo, userItem, accessToken);
            }

            @Override
            public void onFailure(FacebookException error) {
                startView.didLoadFacebookFailure(error.toString());
            }
        });
    }

    private void handleFacebookUserInfo(final JSONObject userInfo, final UserItem userItem, final String accessToken) {
        try {
            String name = userInfo.getString("name");
            if (userInfo.has("birthday")) {
                String birthday = userInfo.getString("birthday");
                if (!isAbove18Age(birthday)) {
                    startView.didBirthdayIsBelow18();
                }
                userItem.setDateOfBirth(birthday);
            } else {
                userItem.setDateOfBirth(null);
            }
            String gender = userInfo.getString("gender");
            userItem.setUsername(handleFbName(name));
            userItem.setGender(gender.equalsIgnoreCase("female") ? UserItem.FEMALE : UserItem.MALE);
            this.getAvatarFacebook(userItem, accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String handleFbName(String name) {
        if (name == null || name.trim().length() == 0 ||
                !JapaneseCharacter.isRomajiName(name.trim())) {
            return "";
        }
        return JapaneseCharacter.getInitialName(name.trim());
    }

    private void getAvatarFacebook(final UserItem userItem, String accessToken) {
        FacebookUtils.getGraphPictureRequest(accessToken, new Utility.GraphMeRequestWithCacheCallback() {
            @Override
            public void onSuccess(JSONObject userInfo) {
                ImageItem avatar = parseAvatarFacebook(userInfo);
                userItem.setAvatarItem(avatar);
                requestLoginFacebook(userItem);
            }

            @Override
            public void onFailure(FacebookException error) {
                requestLoginFacebook(userItem);
            }
        });
    }

    private ImageItem parseAvatarFacebook(JSONObject userInfo) {
        ImageItem avatarItem = new ImageItem();
        try {
            JSONObject jData = userInfo.getJSONObject("data");
            boolean isSilhouette = jData.getBoolean("is_silhouette");
            if (!isSilhouette && !"".equals(jData.getString("url"))) {
                String urlPicture = jData.getString("url");
                avatarItem.setOriginUrl(urlPicture);
            } else {
                avatarItem = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return avatarItem;
    }


    private void requestLoginFacebook(UserItem userItem) {
        LoginFacebookTask task = new LoginFacebookTask(getContext(), userItem);
        requestToServer(task);
    }

    @Override
    protected void didResponseTask(BaseTask task) {
        if (task instanceof LoginFacebookTask) {
            this.loginVoIP();
        } else if (task instanceof RegisterTask) {
            UserItem userItem = ((RegisterTask) task).getDataResponse();
            this.startView.didLoginFacebookButNotRegisterOnServer(userItem);
        }
    }

    @Override
    protected void didErrorRequestTask(BaseTask task, int errorCode, String errorMessage) {
        if (Constant.Error.SOCIAL_ID_IS_NOT_EXIST == errorCode) {
            handleNotRegisterFacebook((LoginFacebookTask) task);
        } else if (task instanceof LoginFacebookTask) {
            startView.didLoadFacebookFailure(errorMessage);
        } else {
            Logger.e(tag, errorMessage);
        }
    }

    private void handleNotRegisterFacebook(LoginFacebookTask task) {
        UserItem userItem = task.getUserItem();
        if (userItem.getDateOfBirth() == null) {
            startView.didLoginFacebookMissingBirthday(userItem);
        } else if (userItem.getDateOfBirth() != null) {
            this.registerUser(userItem);
        } else {
            this.registerUser(userItem);
        }
    }

    private void registerUser(UserItem userItem) {
        Date dob = DateTimeUtils.convertStringToDate(userItem.getDateOfBirth(), DateTimeUtils.ENGLISH_FACEBOOK_DATE_FORMAT);
        userItem.setDateOfBirth(DateTimeUtils.convertDateToString(dob, DateTimeUtils.ENGLISH_DATE_FORMAT));

        RegisterTask registerTask = new RegisterTask(context, userItem);
        requestToServer(registerTask);
    }

    private boolean isAbove18Age(String dateOfBirth) {
        return DateTimeUtils.getCurrentAgeFromDoB(dateOfBirth) >= MIN_AGE;
    }

    @Override
    protected void onDidRegisterVoIPSuccess() {
        this.startView.didLoginVoIP();
    }

    @Override
    protected void onDidRegisterVoIPError(int errorCode, String errorMessage) {
        startView.didErrorVoIP(errorMessage);
    }

}
