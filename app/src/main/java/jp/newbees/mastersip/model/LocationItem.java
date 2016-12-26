package jp.newbees.mastersip.model;

/**
 * Created by ducpv on 12/23/16.
 */

public class LocationItem {



    public static final int PARENT = 0;
    public static final int CHILD = 1;

    public static final int HOKKAIDO = 0;
    public static final int NORTHEAST = -1;
    public static final int KANTO = -2;
    public static final int MIDDLE = -3;
    public static final int KINKI = -4;
    public static final int CHINA = -5;
    public static final int SHIKOKU = -6;
    public static final int KYUSHU = -7;
    public static final int OTHER = -8;

    public static final int START_NORTHEAST = 2;
    public static final int END_NORTHEAST = 7;
    public static final int START_KONTO = 9;
    public static final int END_KONTO = 15;
    public static final int START_MIDDLE = 17;
    public static final int END_MIDDLE = 25;
    public static final int START_KINKI = 27;
    public static final int END_KINKI = 33;
    public static final int START_CHINA = 35;
    public static final int END_CHINA = 39;
    public static final int START_SHIKOKU = 41;
    public static final int END_SHIKOKU = 44;
    public static final int START_KYUSHU = 46;
    public static final int END_KYUSHU = 53;


    private SelectionItem selectionItem;
    private boolean isChecked;
    private int parentId;
    private int type;

    public LocationItem() {
        this.isChecked = false;
    }

    public LocationItem(SelectionItem selectionItem, boolean isChecked, int parentId, int type) {
        this.selectionItem = selectionItem;
        this.isChecked = isChecked;
        this.parentId = parentId;
        this.type = type;
    }



    public SelectionItem getSelectionItem() {
        return selectionItem;
    }

    public void setSelectionItem(SelectionItem selectionItem) {
        this.selectionItem = selectionItem;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
