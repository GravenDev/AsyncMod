package fr.redstom.gravenlevelling.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LevelUtils {

    @Value("${xp.min_per_message}")
    private int minPerMessage = 2;

    @Value("${xp.max_per_message}")
    private int maxPerMessage = 5;

    @Value("${xp.min_message_length}")
    private int minMessageLength = 10;

    @Value("${xp.max_message_length}")
    private int maxMessageLength = 100;

    public long xpForNextLevelAt(long level) {
        return Math.round(5 * Math.pow(level, 2) + (50 * level) + 100);
    }

    public long flattenMessageLengthIntoGain(double messageLength) {
        if(messageLength < minMessageLength) {
            return minPerMessage;
        }
        if (messageLength > maxMessageLength) {
            return maxPerMessage;
        }

        return Math.round(minPerMessage + ((messageLength - minMessageLength) * (maxPerMessage - minPerMessage) / (maxMessageLength - minMessageLength)));
    }

    public String formatExperience(long xp, long level) {
        long totalXp = xpForNextLevelAt(level);

        return STR."\{formatNumber(xp)}/\{formatNumber(totalXp)}";
    }


    private String formatNumber(long number) {
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
