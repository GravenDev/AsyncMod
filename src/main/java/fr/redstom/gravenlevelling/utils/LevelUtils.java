package fr.redstom.gravenlevelling.utils;

public class LevelUtils {

    public static final int MIN_PER_MESSAGE = 25;
    public static final int MAX_PER_MESSAGE = 50;

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
}
