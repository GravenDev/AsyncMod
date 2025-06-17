package fr.itsasync.moderation.util;

import net.dv8tion.jda.api.EmbedBuilder;

public class Embeds {

    public static EmbedBuilder error(String description) {
        return new EmbedBuilder()
                .setTitle("❌ Erreur !")
                .setDescription(description)
                .setColor(AsyncColors.RED);
    }
}
