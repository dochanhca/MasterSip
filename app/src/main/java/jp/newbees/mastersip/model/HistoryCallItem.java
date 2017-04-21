package jp.newbees.mastersip.model;

import android.content.Context;

import jp.newbees.mastersip.R;
import jp.newbees.mastersip.utils.Logger;

/**
 * Created by vietbq on 4/18/17.
 */

public class HistoryCallItem {

    private final int CALL_LOG_RESULT_FAILURE = 0;
    private final int CALL_LOG_RESULT_VOICE = 1;
    private final int CALL_LOG_RESULT_VIDEO = 2;

    private UserItem userItem;
    private String lastTimeCallLog;
    private int duration;
    private int callLogResultType;

    public HistoryCallItem() {

    }

    public UserItem getUserItem() {
        return userItem;
    }

    public void setUserItem(UserItem userItem) {
        this.userItem = userItem;
    }

    public String getLastTimeCallLog() {
        return lastTimeCallLog;
    }

    public void setLastTimeCallLog(String lastTimeCallLog) {
        this.lastTimeCallLog = lastTimeCallLog;
    }

    public String getDuration(Context context) {
        String result;
        if (callLogResultType == CALL_LOG_RESULT_FAILURE) {
            result =  context.getResources().getString(R.string.call_fail);
        }else {
            String strDuration = getDurationCall(this.duration, context);
            String formatDurationCall = context.getString(R.string.format_duration_call);
            result =  String.format(formatDurationCall, strDuration);
        }
        return result;
    }

    private String getDurationCall(int totalSeconds, Context context) {
        String result;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        Logger.e("HistoryCallItem", "Duration " + totalSeconds);
        if (hours > 0) {
            String format = context.getString(R.string.format_duration_full);
            result = String.format(format, hours, minutes, seconds);
        } else {
            if (minutes > 0) {
                String format = context.getString(R.string.format_duration_shorter);
                result = String.format(format, minutes, seconds);
            }else {
                String format = context.getString(R.string.format_duration_shortest);
                result = String.format(format, seconds);
            }
        }
        return result;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCallLogResultType(int callLogResultType) {
        this.callLogResultType = callLogResultType;
    }

    public int getDrawableCallLogType() {
        switch (callLogResultType) {
            case CALL_LOG_RESULT_VOICE:
                return R.drawable.ic_small_gray_voice;
            case CALL_LOG_RESULT_VIDEO:
                return R.drawable.ic_small_gray_video;
            default:
                return R.drawable.ic_gray_small_cancel_call;
        }
    }
}
