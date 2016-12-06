package jp.newbees.mastersip.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by vietbq on 12/6/16.
 */

public class UserItem {
    @NonNull
    private String username;
    @NonNull
    private LocationItem location;
    private JobItem jobItem;
    private AvailableTimeItem availableTimeItem;
    private String typeOfBoy;
    private String charmingPoint;
    private String memo;
    private int gender;
    private String dateOfBirth;
    private int coin;
    private int status;
    @Nullable
    private String email;

    @Nullable
    private ImageItem avatarItem;

    @Nullable
    private String avatarUrl;
    @Nullable
    private String facebookId;
    @NonNull
    private SipItem sipItem;
}
