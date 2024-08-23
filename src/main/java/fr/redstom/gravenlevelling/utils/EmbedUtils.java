package fr.redstom.gravenlevelling.utils;

import net.dv8tion.jda.api.EmbedBuilder;

public class EmbedUtils {

    public static EmbedBuilder error(String description) {
        return new EmbedBuilder()
                .setTitle("❌ Erreur !")
                .setDescription(description)
                .setColor(GravenColors.RED);
    }

}
