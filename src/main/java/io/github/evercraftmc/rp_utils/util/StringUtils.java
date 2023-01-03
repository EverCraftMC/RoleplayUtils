package io.github.evercraftmc.rp_utils.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringUtils {
    public static List<String> matchPartial(String token, List<String> originals) {
        List<String> results = new ArrayList<String>();

        for (String string : originals) {
            if (string.toLowerCase().startsWith(token.toLowerCase())) {
                results.add(string);
            }
        }

        return results;
    }

    public static String toTittleCase(String string) {
        StringBuilder converted = new StringBuilder();

        int i = 0;
        for (char ch : string.toCharArray()) {
            if (i == 0 || string.toCharArray()[i - 1] == ' ') {
                ch = Character.toTitleCase(ch);
            }

            converted.append(ch);

            i++;
        }

        return converted.toString();
    }

    private static final String[] currencyLetters = new String[] { "", "", "M", "B", "T", "Qa", "Qu", "S" };

    public static String formatCurrencyMin(double currency) {
        String currencyString = NumberFormat.getCurrencyInstance(Locale.US).format(currency);
        String currencyMin = currencyString;
        if (currency > 999999) {
            currencyMin = currencyString.split(",")[0] + "." + currencyString.split(",")[1].substring(0, 2) + currencyLetters[currencyString.split(",").length - 1];
        }

        return currencyMin;
    }
}