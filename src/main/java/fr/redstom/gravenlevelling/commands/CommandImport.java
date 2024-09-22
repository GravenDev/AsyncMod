package fr.redstom.gravenlevelling.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.redstom.gravenlevelling.jpa.services.GravenGuildSettingsService;
import fr.redstom.gravenlevelling.jpa.services.GravenMemberService;
import fr.redstom.gravenlevelling.jpa.services.GravenUserService;
import fr.redstom.gravenlevelling.utils.imports.ImportEntry;
import fr.redstom.gravenlevelling.utils.jda.Command;
import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;
import java.io.InputStream;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.context.annotation.Import;

@Command
@RequiredArgsConstructor
public class CommandImport implements CommandExecutor {

    private final GravenGuildSettingsService guildSettingsService;
    private final ObjectMapper mapper;
    private final GravenMemberService memberService;
    private final GravenUserService userService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("import", "Importer des niveaux depuis des données existantes")
                .addSubcommands(
                        new SubcommandData("json", "Importer des niveaux depuis un fichier JSON")
                                .addOption(OptionType.ATTACHMENT, "file", "Fichier JSON à importer dans le format [{ id: long, level: long }]", true),
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
        Message.Attachment file = event.getOption("file").getAsAttachment();

        if (!"json".equals(file.getFileExtension())) {
            event.reply("Le fichier doit être au format JSON !").queue();
            return;
        }

        InteractionHook hook = event.deferReply().complete();
        file.getProxy().download().thenAccept(stream -> this.decode(event, hook, stream));
    }

    @SneakyThrows
    private void decode(SlashCommandInteractionEvent event, InteractionHook hook, InputStream stream) {
        List<ImportEntry> model;
        try {
            model = mapper.readValue(stream, new TypeReference<List<ImportEntry>>() {});
        } catch (Exception e) {
            hook.editOriginal("Erreur lors de la lecture du fichier : " + e.getMessage()).complete();
            return;
        }
        hook.editOriginal("Importation des niveaux en cours...").complete();

        Guild guild = event.getGuild();
        for (ImportEntry entry : model) {
            memberService.getMemberByGuildAndMemberId(guild, entry.id(), entry.level());
        }
        hook.editOriginal("Importation des niveaux terminée !").complete();
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
