package jp.newbees.mastersip.utils;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final class JSON {
        public static String kDeviceId = "device_id";
        public static String kPassword = "password";
        public static String kEmail = "email";
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
        public static String kUserId = "user_id";
        public static String kUploadType = "upload_for";
        public static String kHandleName = "handle_name";
        public static String kPhoneNumber = "phonenumber";
        public static String kProvinceId = "province_id";
        public static String kAvatarId = "avatar_id";
        public static String kJobId = "job_id";
        public static String kTypeId = "type_id";
        public static String kTypeBoy = "type_boy";
        public static String kCharmPoint ="charm_point";
        public static String kFreeTime = "free_time";
        public static String kSlogan = "slogan";
        public static String kExtension = "extension";
    }

    public static final class API {
        private static final String DEVELOPMENT_IP = "52.197.14.30";
        private static final String PRODUCTION_IP = "52.197.138.1";

        public static final String VERSION = "v1";
        public static final String PREFIX_URL = "sip_api/public/api";
        public static String BASE_URL;

        public static final String LOGIN_BY_EMAIL = "login";
        public static final String REGISTER = "register";
        public static final String UPLOAD_IMAGE = "upload_image";
        public static final String UPDATE_REGISTER_PROFILE = "user/update";

        public static void initBaseURL() {
            BASE_URL = Application.DEBUG ? DEVELOPMENT_IP : PRODUCTION_IP;
        }

        public static final String TIP_PAGE = "http://" + DEVELOPMENT_IP + "/sip_api/webview/tips/";
        public static final String TIP_PAGE_DIRECTION = "sip://sipBackDevice";
    }

    public final class Error {
        public static final int SERVER_BUSY = 100000;
        public static final int REQUEST_TIMEOUT = 100001;
        public static final int UNKNOWN_ERROR = 100003;
        public static final int PARSE_ERROR = 100004;
        public static final int PARSE_PARAM_ERROR = 100005;
        public static final int MISSING_FACEBOOK_ID = 1006;
        public static final int INVALID_DEVICE_ID = 1001;
        public static final int MAX_CALL_ERROR = 1200;
        public static final int USER_BUSY = 1201;
        public static final int SAME_GENDER = 1202;
        public static final int RECEIVER_NOT_EXISTS = 1203;
        public static final int ROOM_CHAT_IS_NOT_EXIST = 1403;
        public static final int OUT_OF_MONEY = 1204;
        public static final int EXTENSION_OFFLINE = 1206;
        public static final int USER_NOT_EXIST = 1011;
        public static final int SOCIAL_ID_IS_NOT_EXIST = 1100;
        public static final int EMAIL_OR_PASS_IS_WRONG = 1044;
        public static final int WRONG_CODE_RESET_PASS = 1039;
        public static final int EMAIL_USED_BY_ANOTHER = 1008;
        public static final int EMAIL_IS_NOT_EXIST = 1041;
        public static final int RESET_CODE_IS_NOT_MATCH = 1501;
        public static final int RESET_CODE_IS_NOT_EXIST = 1500;
        public static final int INVALID_GENDER = 1002;
        public static final int NO_MESSAGE_TO_UPDATE_STATUS = 1410;
        public static final int INVALID_PASSWORD = 1009;
        public static final int INVALID_EMAIL = 1007;
        public static final int INVALID_TOKEN = 9999;
        public static final int NO_NETWORK = 50;
        public static final int REQUEST_TIME_OUT = -2102;
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
