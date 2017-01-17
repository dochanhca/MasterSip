package jp.newbees.mastersip.utils;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final class JSON {
        public final static String DEVICE_ID = "device_id";
        public final static String PASSWORD = "password";
        public final static String EMAIL = "email";
        public final static String CLIENT_AUTH_ID = "client_auth_id";

        public final static String REGIST_TOKEN = "regist_token";
        public final static String K_BIRTHDAY = "birthday";
        public final static String K_GENDER = "gen";
        public final static String SOCIAL_ID = "social_id";
        public final static String OS_VERSION = "os_version";
        public final static String APP_VERSION = "app_version";
        public final static String DEVICE_INFO = "device_info";
        public final static String CODE = "code";
        public final static String K_MESSAGE = "message";
        public final static String DATA = "data";
        public final static String USER_ID = "user_id";
        public final static String K_UPLOAD_TYPE = "upload_for";
        public final static String HANDLE_NAME = "handle_name";
        public final static String K_PHONE_NUMBER = "phonenumber";
        public final static String PROVINCE_ID = "province_id";
        public final static String AVATAR_ID = "avatar_id";
        public final static String JOB_ID = "job_id";
        public final static String TYPE_ID = "type_id";
        public final static String TYPE_BOY = "type_boy";
        public final static String CHARM_POINT ="charm_point";
        public final static String FREE_TIME = "free_time";
        public final static String SLOGAN = "slogan";
        public final static String K_EXTENSION = "extension";
        public final static String ABOVE_AGE = "above_age";
        public final static String BELOW_AGE = "below_age";
        public final static String PROVINCES = "provinces";
        public final static String ORDER_BY = "order_by";
        public final static String K_LOGIN_24_HOUR_AGO = "limit_online_ago";
        public final static String K_FILTER_TYPE = "type";
        public final static String NEXT_PAGE = "next_page";
        public final static String USERS = "users";
        public final static String ID = "id";
        public final static String AVATAR = "avatar";
        public final static String RELATIONS = "relations";
        public final static String FOLLOWED = "followed";
        public final static String ONLINE_NOTIFICATION = "online_notification";
        public final static String JOB_NAME = "job_name";
        public final static String LAST_LOGIN = "last_login";
        public final static String STATUS = "status";
        public final static String K_USER_GENDER = "gender";
        public final static String EXTEND_INFO = "extend_info";
        public final static String FAVORITE_TYPE = "favorite_type";
        public final static String PATH = "path";
        public final static String K_DATE = "date";

        public final static String LAT = "lat";
        public final static String LONG = "long";

        public final static String K_RECEIVE_PROVINCE_ID = "provinceId";
        public final static String K_PROVINCE_NAME = "provinceName";

        public final static String IMAGE_ID = "image_id";
        public final static String IMAGE_PATH_FULL = "image_path_full";
        public final static String IMAGE_PATH_THUMB = "image_path_thumb";
        public final static String PROVINCE = "province";

        //TODO : Duplicate
        public final static String USER_PROVINCE_ID = "province_id";
        public final static String PROVINCE_NAME = "province_name";
        public final static String NAME = "name";
        public final static String K_TYPE = "type";
        public final static String CONTENT = "content";
        public final static String EXTENSION_SRC = "extension_src";
        public final static String EXTENSION_DEST = "extension_dest";
        public final static String ROOM_TYPE = "room_type";
        public final static String RESPONSE = "response";
        public final static String DELETED = "deleted";
        public final static String SENDER = "sender";
        public final static String TEXT = "text";
        public final static String ROOM_ID = "room_id";
        public final static String kFromExtension = "from_extension";

        public static final String CALLER = "caller";
        public static final String RECEIVER = "receiver";
        public static final String TYPE = "type";
        public static final String KIND = "kind";
        public static final String CALL_WAIT_ID = "call_wait_id";
        public static final String MESSAGE_ID = "message_id";
        public static final String MIN_POINT = "min_point";

        public static final String ACTION = "action";
        public static final String EXTENSION_ID = "extension_id";
        public static final String URL_AVATAR = "url_avatar";
        public static final String EXTENSION = "extension";
        public static final String RECEIVER_STATUS = "receiver_status";

        public static final String COINT = "coint";
        public static final String WAIT_CALL_ID = "wait_call_id";
        public static final String SECRET_KEY = "secretKey";
    }

    public static final class API {
        public static final int AVAILABLE_CALL = 1;
        public static final int NEW_USER = 2;
        public static final int ALL_USER = 3;

        public static final int VOICE_CALL = 1;
        public static final int VIDEO_CALL = 2;
        public static final int VIDEO_CHAT_CALL = 7;

        public static final int CALL_FROM_CHAT_ROOM = 1;
        public static final int CALL_FROM_OTHER = 2;

        private static final String DEVELOPMENT_IP = "52.197.14.30";
//        private static final String DEVELOPMENT_IP = "52.197.138.1";
        private static final String PRODUCTION_IP = "52.197.138.1";

        public static String BASE_URL;
        public static final String PROTOCOL = "http";

        public static final String VERSION = "v1";
        public static final String PREFIX_URL = "sip_api/public/api";
        public static final String FILTER_USER = "users";
        public static final String LOGIN_BY_EMAIL = "login";
        public static final String REGISTER = "register";
        public static final String UPLOAD_IMAGE = "upload_image";
        public static final String UPDATE_REGISTER_PROFILE = "user/update";
        public static final String GET_PROVINCE = "provinces/province";
        public static final String SEARCH_BY_NAME_URL = "searchuser";
        public static final String CHAT_MESSAGE = "chat";
        public static final String CHECK_CALL = "calls/check-call";
        public static final String CHECK_TYPE_INCOMING_CALL = "calls/check-type-call";
        public static final String UPDATE_STATE_MESSAGE_URL = "message/status/update";
        public static final String CANCEL_CALL_URL = "calls/cancel-call";
        public static final String LOGIN_FACEBOOK_URL = "loginfb";

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
//
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
        public static final int REQUEST_TIME_OUT = REQUEST_TIMEOUT;
        public static final int SC_INTERNAL_SERVER_ERROR = 500;
        public static final int WRONG_ORDER_BY_CODE = 3003;
        public static final int VOIP_ERROR = 5000;
    }

    public final class Application {
        public static final boolean DEBUG = true;
        public static final boolean SHOW_DATA_REQUEST = true;
        public static final String PREFERENCE_NAME = "MasterSip";
        public static final String AUTHORIZATION = "AUTHORIZATION";
        public static final String USER_ITEM = "UserItem";
        public static final String REGISTER_TOKEN = "RegisterToken";
        public static final String LOGIN_FLAG = "Login_Flag";

        public static final String DEFAULT_LANGUAGE = "ja";
        public static final int MIN_AGE = 18;
        public static final int MAX_AGE = 120;
        public static final String SETTING_FILTER = "SETTING_FILTER";
        public static final int LAST_LOGIN = 1;
        public static final int LAST_REGISTER = 2;
    }

    public class SOCKET {
        public static final String ACTION_ABOUT_RUN_OUT_OF_COINS = "about_to_run_out_of_coins";
        public static final String ACTION_CHATTING = "chatting";
        public static final String ACTION_COIN_CHANGED = "coin_changed";
        public static final String ACTION_CHANGE_MESSAGE_STATE = "update_message_status_v2";
    }

    public class EVENT {
        public static final String ACTION_CHATTING = "ACTION_CHATTING";
    }
}
