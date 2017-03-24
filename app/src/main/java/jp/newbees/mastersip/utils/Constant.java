package jp.newbees.mastersip.utils;

import java.text.DecimalFormat;

/**
 * Created by vietbq on 12/6/16.
 */

public final class Constant {

    public static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("####,###,###");

    public static final class JSON {
        public static final String IMG_ID = "img_id";
        public static final String VIDEO_CALL_SET = "video_call_set";
        public static final String VOICE_CALL_SET = "voice_call_set";
        public static final String GIFTS = "gifts";
        public static final String PRICE = "price";
        public static final String NEXT = "next";
        public static final String COUNT_ROOM_UNREAD = "count_room_unread";
        public static final String LIST_CHAT_ROOMS = "list_chatrooms";
        public static final String INTERACTION_USER = "interaction_user";
        public static final String LAST_MSG_DESCRIPTION = "last_msg_description";
        public static final String LAST_MSG_TIMESTAMP = "last_msg_timestamp";
        public static final String GIFT_ID = "gift_id";
        public static final String GIFT = "gift";
        public static final String CONTEXT = "context";
        public static final String DIS_EXTENSION = "dis_extension";
        public static final String PLATFORM = "platform";
        public static final String PASSWORD_CONFIRMATION = "password_confirmation";
        public static final String EMAIL_OLD = "email_old";
        public static final String PASSWORD_OLD = "password_old";
        public static final String EMAIL_NEW = "email_new";
        public static final String PASSWORD_NEW = "password_new";
        public static final String NEW_PASS = "new_password";
        public static final String ALL = "all";
        public static final String ARR_CHAT_ROOM_ID = "arr_chat_id";
        public static final String CHAT_SET = "chat_set";
        public static final String VERSION = "version";
        public static final String LAST_ROOM_ID = "last_room_id";
        public static final String CREATED_AT = "created_at";
        public static final String TOTAL_UNREAD = "total_unread";
        public static final String ROOM_MESS_UNREAD = "room_message_unread";
        public static final String CALL_ID = "call_id";
        public static final String ROOM_FREE = "room_free";
        public static final String CALL = "call";
        public static final String KIND_CALL = "kind_call";
        public static final String DURATION = "duration";
        public static final String PACKAGE_LIST = "package_list";
        public static final String CASH = "cash";
        public static final String DEVICE_TOKEN = "device_token";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String EXTENSION_FROM = "extension_from";


        private JSON() {
            //Prevent init constructor
        }

        public static final String PAGINATE = "paginate";
        public static final String IMAGE_STATUS = "image_status";
        public static final String PAGE = "page";
        public static final String POINT = "point";
        public static final String MY_INFO = "my_info";
        public static final String THUMBNAIL = "thumbnail";

        public static final String DEVICE_ID = "device_id";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String CLIENT_AUTH_ID = "client_auth_id";

        public static final String REGIST_TOKEN = "regist_token";
        public static final String TOKEN_DEVICE = "token_device";
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
        public static final String CHARM_POINT = "charm_point";
        public static final String FREE_TIME = "free_time";
        public static final String SLOGAN = "slogan";
        public static final String ABOVE_AGE = "above_age";
        public static final String BELOW_AGE = "below_age";
        public static final String PROVINCES = "provinces";
        public static final String ORDER_BY = "order_by";
        public static final String LOGIN_24_HOUR_AGO = "limit_online_ago";
        public static final String FILTER_TYPE = "type";
        public static final String NEXT_PAGE = "next_page";
        public static final String NEXT_ID = "next_id";
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
        public static final String THUMB = "thumbnail";
        public static final String DATE = "date";

        public static final String LAT = "lat";
        public static final String LONG = "long";

        public static final String RECEIVE_PROVINCE_ID = "provinceId";
        public static final String PROVINCE_NAME_V2 = "provinceName";

        public static final String IMAGE_ID = "image_id";
        public static final String IMAGE_PATH_FULL = "image_path_full";
        public static final String IMAGE_PATH = "image_path";
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
        public static final String SETTING = "setting";
        public static final String USER = "user";

        public static final String VOICE_CALL = "voice_call";
        public static final String VIDEO_CALL = "video_call";
        public static final String CHAT = "chat";
        public static final String SECRET_KEY = "secretKey";
        public static final String LIST_MESSAGES = "list_messages";
        public static final String MESSAGES = "messages";
        public static final String MEMBERS = "members";
        public static final String TOTAL_COUNT = "total_count";
        public static final String TOTAL = "total";
        public static final String LIST_IMAGE = "list_images";
        public static final String LIST_PHOTO = "list_photos";
        public static final String IMAGE = "image";

        public static final String DEST_USER_ID = "dest_user_id";

        public static final String ID_ADDON = "id_addon";
        public static final String TRANSECTION = "transection";
        public static final String CREATE_AT = "created_at";
        public static final String REGISTRATION_ID = "registration_id";
        public static final String OS = "os";
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

        public static final int TYPE_UPLOAD_VOICE = 1;
        public static final int TYPE_UPLOAD_VIDEO = 3;
        public static final int TYPE_UPLOAD_IMAGE = 4;


        private static final String DEVELOPMENT_IP = "52.199.112.26";
        //                private static final String DEVELOPMENT_IP = "52.197.138.1";
        private static final String PRODUCTION_IP = "52.197.138.1";
        public static final String GET_PROFILE = "profile";

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
        public static final String PHOTOS = "photos";
        public static final String MY_PROFILE = "my/profile";
        public static final String LOGOUT = "logout";
        public static final String LIST_MY_PHOTOS = "my/photos";
        public static final String SAVE_FILE_CHAT = "save_file_chat";
        public static final String DELETE_IMAGE = "image/delete";
        public static final String CHAT_HISTORY = "messages";
        public static final String SETTING_CALL = "setting_call";
        public static final String FOLLOW = "follow";
        public static final String UN_FOLLOW = "unfollow";
        public static final String LIST_ROOM = "chatrooms";
        public static final String GIFTS_LIST = "gifts";
        public static final String SEND_GIFT = "gift/send";
        public static final String UPDATE_IMAGE = "image/update";
        public static final String REQUEST_ENABLE_VOICE_CALL = "putmess";
        public static final String CHATTING_PHOTO = "chatting/photos";
        public static final String UPDATE_USER_PASS = "adduserpass/update-user-pass";
        public static final String CHECK_CODE = "adduserpass/check-code";
        public static final String FORGOT_PASS = "forgotpass/forgot-pass";
        public static final String CHANGE_PASS = "forgotpass/change-pass";
        public static final String CHANGE_EMAIL_BACKUP = "adduserpass/change-user-pass-new";
        public static final String CHOSE_PAYMENT_TYPE = "http://52.197.14.30/thaihv_api/public/webview/payment?";
        public static final String MARK_MESSAGE_AS_READ = "message/mark_as_read";
        public static final String DELETE_CHAT_ROOM = "chatrooms/delete";
        public static final String SEND_PURCHASE_RESULT = "payment/os/2/addon";
        public static final String REGISTER_PUSH_NOTIFY = "notify/register-push-notifications";
        public static final String RECONNECT_CALL = "calls/reconnect-call";
        public static final String JOIN_TO_CALL = "calls/join-to-call";
        public static final String PAYMENT_PACKAGE_LIST = "payment/list_package";

        public static void initBaseURL() {
            BASE_URL = !Application.DEBUG ? DEVELOPMENT_IP : PRODUCTION_IP;
        }

        public static final String TIP_PAGE = "http://" + DEVELOPMENT_IP + "/sip_api/webview/tips/";
        public static final String TIP_PAGE_DIRECTION = "sip://sipBackDevice";
        public static final String BIT_CASH_PAYMENT_SUCCESS = "sip://" + Application.BIT_CASH + "/status="
                + "success";
        public static final String CREDIT_CASH_PAYMENT_SUCCESS = "sip://" + Application.CREDIT_CARD + "/status="
                + "success";
    }

    public final class Error {
        public static final int NOT_ENOUGH_POINT = 1204;

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
        public static final int RESET_CODE_INVALID = 2011;
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
        public static final int IN_APP_PURCHASE_FAIL = 5001;
        public static final int IN_APP_PURCHASE_NOT_SUCCESS = 5002;
        public static final int IN_APP_PURCHASE_CANCEL = 5003;
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
        public static final String LOGIN_VOIP_FLAG = "LOGIN_VOIP_FLAG";

        public static final String DEFAULT_LANGUAGE = "ja";
        public static final int MIN_AGE = 18;
        public static final int MAX_AGE = 120;
        public static final String SETTING_FILTER = "SETTING_FILTER";
        public static final String CHATTING_FLAG = "ChatingFlag";
        public static final int LAST_LOGIN = 1;
        public static final int LAST_REGISTER = 2;

        public static final int MAX_IMAGE_SIZE = 1024;
        public static final int ANDROID = 2;
        public static final String CREDIT_CARD = "CREDITCARD";
        public static final String BIT_CASH = "BITCASH";

        public static final int MIN_COIN_FOR_CALL = 10;
    }

    public class SOCKET {

        private SOCKET() {
            //Prevent init object
        }

        public static final String ACTION_BUSY_CALL = "BUSY_CALL";
        public static final String ACTION_CANCEL_CALL = "CANCEL_CALL";
        public static final String ACTION_CHANGE_CALLING_STATUS = "CHANGE_CALLING_STATUS";
        public static final String ACTION_ABOUT_RUN_OUT_OF_COINS = "about_to_run_out_of_coins";
        public static final String ACTION_RUN_OUT_OF_COINS = "run_out_of_coins";
        public static final String ACTION_CHATTING = "chatting";
        public static final String ACTION_COIN_CHANGED = "coin_changed";
        public static final String ACTION_CHANGE_MESSAGE_STATE = "update_message_status";
        public static final String ACTION_HANG_UP_FOR_GIRL_BLOCK_ZERO = "Hangup_for_girl_block_zero";
        public static final String ACTION_ADMIN_HANG_UP = "ADMIN_HANG_UP";

        public static final int STATUS_CALLING_WAITING = 1;
        public static final int STATUS_CALLING_CANCELED = 2;
        public static final int STATUS_CALLING_CONNECTED = 3;
        public static final int STATUS_CALLING_MISSING = 4;
        public static final int STATUS_CALLING_END = 5;
    }

    public static class InAppBilling {
        public static final String SKU_TYPE_01 = "1";
        public static final String SKU_TYPE_02 = "2";
        public static final String SKU_TYPE_03 = "3";
        public static final String SKU_TYPE_04 = "4";

        public static final String[] SKUS = {SKU_TYPE_01, SKU_TYPE_02, SKU_TYPE_03, SKU_TYPE_04};
        public static final int RC_REQUEST = 10001;

        public static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqnLWbhSgEG1u+0PZ3frkI7EeRge5iD8gOthik3llKyCbSpHPCY/9YjMIXrbe97XQZj7vp2MUeX4DHMB7sBNHT/T2rcpHvoezTZrUiUEPb4rTodEd9c1Ks1pcOEJ+cZpBRHOVRkG1Y+ZM4ftvvYnfsQE9xdaGAhWm+BJDoFmBP9YNwSyLI4WC07qp4s38a9hpB3XWXJG6p20oCyhVAyY/vazW53BpWlupyGpfI4C5Au8rwOGbJ/2scl0xAfKsxQxj2pNPU7yrs1XLDUjdPiS7swSuVp803Fu8v5o1CWRnQhEXi/XPjtEqSM/MRS04JP3PoV/YjrdgYboskqIRbRbZcwIDAQAB";

    }
}
