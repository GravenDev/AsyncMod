package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jpa.services.GravenGuildSettingsService;
import fr.redstom.gravenlevelling.utils.jda.Command;
import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@Command
@RequiredArgsConstructor
public class CommandImport implements CommandExecutor {

    private final GravenGuildSettingsService guildSettingsService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("import", "Importer des niveaux depuis des données existantes")
                .addSubcommands(
                        new SubcommandData("json", "Importer des niveaux depuis un fichier JSON")
                                .addOption(OptionType.ATTACHMENT, "fichier", "Fichier JSON à importer", true),
                        new SubcommandData("roles", "Active ou désactive l'importation depuis des rôles Discord")
                                .addOption(OptionType.BOOLEAN, "enable", "Activer ou désactiver l'importation depuis des rôles Discord", true)
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "json" -> importFromJson(event);
            case "roles" -> importFromRoles(event);
        }
    }

    public void importFromJson(SlashCommandInteractionEvent event) {
        event.reply("Importation depuis un fichier JSON").queue();
    }

    public void importFromRoles(SlashCommandInteractionEvent event) {
        boolean enable = event.getOption("enable").getAsBoolean();

        guildSettingsService.applyAndSave(
                event.getGuild(),
                settings -> settings.toBuilder()
                        .autoLevelGrant(enable)
                        .build()
        );

        event.reply("L'importation depuis des rôles Discord a été " + (enable ? "activée" : "désactivée") + " !")
                .queue();
    }
}
