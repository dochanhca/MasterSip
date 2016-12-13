package jp.newbees.mastersip.utils;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final class JSON {
        public static String kDeviceId  = "device_id";
        public static String kPassword  = "password";
        public static String kEmail     = "email";
        public static String kClientAuthID = "client_auth_id";

        public static String kRegisterToken = "regist_token";
        public static String kBirthday = "birthday";
        public static String kGender = "gen";
        public static String kSocialId = "social_id";
        public static String kOSVersion = "os_version";
        public static String kAppVersion = "app_version";
        public static String kDeviceInfo = "device_info";
        public static String kCode = "code";
        public static String kMessage = "message";
        public static String kData = "data";
    }

    public static final class API {
        private static final String DEVELOPMENT_IP    = "52.197.14.30";
        private static final String PRODUCTION_IP     = "52.197.138.1";

        public static final String VERSION            = "v1";
        public static final String  PREFIX_URL        = "sip_api/public/api";
        public static String BASE_URL;

        public static final String LOGIN_BY_EMAIL = "login";
        public static final String REGISTER = "register";

        public static void initBaseURL(){
            BASE_URL = Application.DEBUG ? DEVELOPMENT_IP : PRODUCTION_IP;
        }
    }

    public final class Error {
        public static final int SERVER_BUSY         = 100000;
        public static final int REQUEST_TIMEOUT     = 100001;
        public static final int NO_NETWORK          = 100002;
        public static final int UNKNOWN_ERROR       = 100003;
        public static final int PARSE_ERROR         = 100004;
        public static final int PARSE_PARAM_ERROR   = 100005;
        public static final int MISSING_FACEBOOK_ID = 1006;
    }

    public final class Application {
        public static final boolean DEBUG = true;
        public static final boolean SHOW_DATA_REQUEST = true;
        public static final String PREFERENCE_NAME = "MasterSip";
        public static final String AUTHORIZATION = "AUTHORIZATION";
        public static final String USER_ITEM = "UserItem";
        public static final String REGISTER_TOKEN = "RegisterToken";

        public static final String DEFAULT_LANGUAGE = "ja";
        public static final int MIN_AGE = 18;
    }

}
