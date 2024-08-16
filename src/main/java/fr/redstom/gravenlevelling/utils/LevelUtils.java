package fr.redstom.gravenlevelling.utils;

public class LevelUtils {

    public static final int MIN_PER_MESSAGE = 200;
    public static final int MAX_PER_MESSAGE = 200;

    public static long xpForNextLevelAt(long level) {
        return Math.round(5 * Math.pow(level, 2) + (50 * level) + 100);
    }

    public static long flattenMessageLengthIntoGain(double messageLength) {
        if(messageLength < 10) {
            return MIN_PER_MESSAGE;
        }
        if (messageLength > 100) {
            return MAX_PER_MESSAGE;
        }

        return Math.round(MIN_PER_MESSAGE + ((messageLength - 10) * (MAX_PER_MESSAGE - MIN_PER_MESSAGE) / 90));
    }

    public static String formatExperience(long xp, long level) {
        long totalXp = xpForNextLevelAt(level);

        return formatNumber(xp) + "/" + formatNumber(totalXp);
    }


    private static String formatNumber(long number) {
        if (number >= 1_000_000_000) {
            return String.format("%.1fB", number / 1_000_000_000.0);
        } else if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 500) {
            return String.format("%.1fk", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}
