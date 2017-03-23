package jp.newbees.mastersip.utils;

/**
 * Created by ducpv on 3/22/17.
 */

public class JapaneseCharacter {

    /**
     * Determines if this character could be used as part of
     * a romaji character.
     */
    public static boolean isRomaji(char c) {
        if (('\u0041' <= c) && (c <= '\u0090'))
            return true;
        else if (('\u0061' <= c) && (c <= '\u007a'))
            return true;
        else if (('\u0021' <= c) && (c <= '\u003a'))
            return true;
        else if (('\u0041' <= c) && (c <= '\u005a'))
            return true;
        else
            return false;
    }

    public static boolean isRomajiName(String name) {
        for (char c : name.toCharArray()) {
            if (!isRomaji(c) && c != '\u0020') {
                return false;
            }
        }
        return true;
    }

    public static String getInitialName(String fullName) {
        StringBuilder initialName = new StringBuilder();

        String[] words = fullName.split(" ");
        for (String word : words) {
            if (word.length() > 0) {
                initialName.append(word.toUpperCase().charAt(0))
                        .append(".");
            }
        }
        // Remove last '.' character
        initialName.deleteCharAt(initialName.length() - 1);
        return initialName.toString();
    }
}
