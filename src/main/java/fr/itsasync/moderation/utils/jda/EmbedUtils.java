package fr.itsasync.moderation.utils.jda;

import fr.itsasync.moderation.utils.AsyncColors;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedUtils {

    public static EmbedBuilder error(String description) {
        return new EmbedBuilder()
                .setTitle("❌ Erreur !")
                .setDescription(description)
                .setColor(AsyncColors.RED);
    }
}
