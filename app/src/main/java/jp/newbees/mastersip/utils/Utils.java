package jp.newbees.mastersip.utils;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.ui.dialog.TextDialog;

/**
 * Created by ducpv on 12/15/16.
 */

public class Utils {

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getAge(String dateOfBirth) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse(dateOfBirth);
            return getAge(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getAge(Date dateOfBirth) {

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();

        int age = 0;

        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("Can't be born in the future");
        }

        age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    public static void showKeyboard(Context c, EditText editText) {
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static Locale getCurrentLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        return locale;
    }

    public static final boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static String validateEmailAndPassword(Context context,
                                                  String email, String password, String repassword) {

        String content = "";
        if (!Utils.isValidEmail(email)) {
            content = context.getResources().getString(R.string.content_wrong_email_format);
        } else if (!isValidPassword(password)) {
            content = context.getResources().getString(R.string.wrong_password_format);
        } else if (!password.equals(repassword)) {
            content = context.getResources().getString(R.string.content_wrong_password_confirmation);
        }
        return content;
    }

    public static boolean isValidPassword(String password) {
        final Pattern hasSpecialChar = Pattern.compile("[^a-zA-Z0-9 ]");
        return (password.length() >= 6 && !hasSpecialChar.matcher(password).find());
    }

    public static void showDialogRegisterSuccess(int requestCode, Fragment fragment) {
        String title = fragment.getContext().getResources().getString(R.string.title_send_confirm_email_backup);
        String content = fragment.getContext().getResources().getString(R.string.content_send_confirm_email_backup_error);
        TextDialog.openTextDialog(fragment, requestCode, fragment.getFragmentManager(), content, title, true);
    }

    public static String getURLChosePaymentType() {
        String authorization = ConfigManager.getInstance().getAuthId();
        String registerToken = ConfigManager.getInstance().getRegisterToken();

        StringBuilder stringBuilder = new StringBuilder("http://52.197.138.1/thaihv_api/public/webview/payment?");
        stringBuilder.append("regist_token=");
        stringBuilder.append(registerToken);
        stringBuilder.append("&");
        stringBuilder.append("client_auth_id=");
        stringBuilder.append(authorization);
        stringBuilder.append("&version_id=2&platform=android");
        return stringBuilder.toString();
    }
}
