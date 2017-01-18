package jp.newbees.mastersip.utils;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final class JSON {
        private JSON() {
            //Prevent init constructor
        }

        public static final  String DEVICE_ID = "device_id";
        public static final  String PASSWORD = "password";
        public static final  String EMAIL = "email";
        public static final  String CLIENT_AUTH_ID = "client_auth_id";

        public static final  String REGIST_TOKEN = "regist_token";
        public static final String BIRTHDAY = "birthday";
        public static final String GENDER = "gen";
        public static final String SOCIAL_ID = "social_id";
        public static final String OS_VERSION = "os_version";
        public static final String APP_VERSION = "app_version";
        public static final String DEVICE_INFO = "device_info";
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String DATA = "data";
        public static final String USER_ID = "user_id";
        public static final String UPLOAD_TYPE = "upload_for";
        public static final String HANDLE_NAME = "handle_name";
        public static final String PHONE_NUMBER = "phonenumber";
        public static final String PROVINCE_ID = "province_id";
        public static final String AVATAR_ID = "avatar_id";
        public static final String JOB_ID = "job_id";
        public static final String TYPE_ID = "type_id";
        public static final String TYPE_BOY = "type_boy";
        public static final String CHARM_POINT ="charm_point";
        public static final String FREE_TIME = "free_time";
        public static final String SLOGAN = "slogan";
        public static final String ABOVE_AGE = "above_age";
        public static final String BELOW_AGE = "below_age";
        public static final String PROVINCES = "provinces";
        public static final String ORDER_BY = "order_by";
        public static final String LOGIN_24_HOUR_AGO = "limit_online_ago";
        public static final String FILTER_TYPE = "type";
        public static final String NEXT_PAGE = "next_page";
        public static final String USERS = "users";
        public static final String ID = "id";
        public static final String AVATAR = "avatar";
        public static final String RELATIONS = "relations";
        public static final String FOLLOWED = "followed";
        public static final String ONLINE_NOTIFICATION = "online_notification";
        public static final String JOB_NAME = "job_name";
        public static final String LAST_LOGIN = "last_login";
        public static final String STATUS = "status";
        public static final String K_USER_GENDER = "gender";
        public static final String EXTEND_INFO = "extend_info";
        public static final String FAVORITE_TYPE = "favorite_type";
        public static final String PATH = "path";
        public static final String DATE = "date";

        public static final String LAT = "lat";
        public static final String LONG = "long";

        public static final String RECEIVE_PROVINCE_ID = "provinceId";
        public static final String PROVINCE_NAME_V2 = "provinceName";

        public static final String IMAGE_ID = "image_id";
        public static final String IMAGE_PATH_FULL = "image_path_full";
        public static final String IMAGE_PATH_THUMB = "image_path_thumb";
        public static final String PROVINCE = "province";

        //TODO : Duplicate
        public static final String USER_PROVINCE_ID = "province_id";
        public static final String PROVINCE_NAME = "province_name";
        public static final String NAME = "name";
        public static final String CONTENT = "content";
        public static final String EXTENSION_SRC = "extension_src";
        public static final String EXTENSION_DEST = "extension_dest";
        public static final String ROOM_TYPE = "room_type";
        public static final String RESPONSE = "response";
        public static final String DELETED = "deleted";
        public static final String SENDER = "sender";
        public static final String TEXT = "text";
        public static final String ROOM_ID = "room_id";
        public static final String kFromExtension = "from_extension";

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

        private API() {
            //Prevent init constructor
        }

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
        private Error() {
            //Prevent init constructor
        }

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
        private Application() {
            //Prevent init object
        }

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
        private SOCKET() {
            //Prevent init object
        }
        public static final String ACTION_ABOUT_RUN_OUT_OF_COINS = "about_to_run_out_of_coins";
        public static final String ACTION_CHATTING = "chatting";
        public static final String ACTION_COIN_CHANGED = "coin_changed";
        public static final String ACTION_CHANGE_MESSAGE_STATE = "update_message_status_v2";
    }

}
