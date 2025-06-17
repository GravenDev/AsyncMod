package fr.itsasync.moderation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import org.slf4j.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtils {

    public static void logLevelChange(
            Logger logger, Guild guild, User user, long oldLevel, long newLevel) {
        logger.info(
                "{} changed level of {} from {} to {} in guild {}",
                user.getAsTag(),
                user.getAsTag(),
                oldLevel,
                newLevel,
                guild.getName());
    }
}
