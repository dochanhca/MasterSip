package jp.newbees.mastersip.utils;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final class JSON {
        public final static String kDeviceId = "device_id";
        public final static String kPassword = "password";
        public final static String kEmail = "email";
        public final static String kClientAuthID = "client_auth_id";

        public final static String kRegisterToken = "regist_token";
        public final static String kBirthday = "birthday";
        public final static String kGender = "gen";
        public final static String kSocialId = "social_id";
        public final static String kOSVersion = "os_version";
        public final static String kAppVersion = "app_version";
        public final static String kDeviceInfo = "device_info";
        public final static String kCode = "code";
        public final static String kMessage = "message";
        public final static String kData = "data";
        public final static String kUserId = "user_id";
        public final static String kUploadType = "upload_for";
        public final static String kHandleName = "handle_name";
        public final static String kPhoneNumber = "phonenumber";
        public final static String kProvinceId = "province_id";
        public final static String kAvatarId = "avatar_id";
        public final static String kJobId = "job_id";
        public final static String kTypeId = "type_id";
        public final static String kTypeBoy = "type_boy";
        public final static String kCharmPoint ="charm_point";
        public final static String kFreeTime = "free_time";
        public final static String kSlogan = "slogan";
        public final static String kExtension = "extension";
        public final static String kAboveAge = "above_age";
        public final static String kBelowAge = "below_age";
        public final static String kProvinces = "provinces";
        public final static String kOrderBy = "order_by";
        public final static String kLogin24HourAgo = "limit_online_ago";
        public final static String kFilterType = "type";
        public final static String kNextPage = "next_page";
        public final static String kUsers = "users";
        public final static String kID = "id";
        public final static String kAvatar = "avatar";
        public final static String kRelationship = "relations";
        public final static String kFollowed = "followed";
        public final static String kNotification = "online_notification";
        public final static String kJobName = "job_name";
        public final static String kLastLogin = "last_login";
        public final static String kStatus = "status";
        public final static String kUserGender = "gender";
        public final static String kExtendInfo = "extend_info";
        public final static String kFavoriteType = "favorite_type";
        public final static String kPath = "path";

        public final static String kLat = "lat";
        public final static String kLong = "long";

        public final static String kReceiveProvinceId = "provinceId";
        public final static String kProvinceName = "provinceName";
    }

    public static final class API {
        public static final int AVAILABLE_CALL = 1;
        public static final int NEW_USER = 2;
        public static final int ALL_USER = 3;

        private static final String DEVELOPMENT_IP = "52.197.14.30";
        private static final String PRODUCTION_IP = "52.197.138.1";
        public static String BASE_URL;

        public static final String VERSION = "v1";
        public static final String PREFIX_URL = "sip_api/public/api";
        public static final String FILTER_USER = "users";
        public static final String LOGIN_BY_EMAIL = "login";
        public static final String REGISTER = "register";
        public static final String UPLOAD_IMAGE = "upload_image";
        public static final String UPDATE_REGISTER_PROFILE = "user/update";
        public static final String GET_PROVINCE = "provinces/province";

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
