package fr.itsasync.moderation.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import net.dv8tion.jda.api.EmbedBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Embeds {

    public static EmbedBuilder error(String description) {
        return new EmbedBuilder()
                .setTitle("❌ Erreur !")
                .setDescription(description)
                .setColor(AsyncColors.RED);
    }
}
