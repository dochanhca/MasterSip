package jp.newbees.mastersip.utils;

/**
 * Created by ducpv on 12/7/16.
 */

public class Enum {

    public enum Gender {
        MALE(0),
        FEMALE(1);

        private int value;

        Gender(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
