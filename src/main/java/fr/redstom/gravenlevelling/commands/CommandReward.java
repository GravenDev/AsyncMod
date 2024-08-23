package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jpa.GravenGuildReward;
import fr.redstom.gravenlevelling.jpa.services.GravenGuildRewardService;
import fr.redstom.gravenlevelling.utils.Command;
import fr.redstom.gravenlevelling.utils.CommandExecutor;
import fr.redstom.gravenlevelling.utils.EmbedUtils;
import fr.redstom.gravenlevelling.utils.GravenColors;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static fr.redstom.gravenlevelling.buttons.DeleteButton.DELETE_BUTTON;

@Command
@RequiredArgsConstructor
public class CommandReward implements CommandExecutor {

    private final GravenGuildRewardService guildRewardService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("rewards", "Configurer les récompenses de niveau")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(
                        new SubcommandData("add", "Ajouter une récompense")
                                .addOption(OptionType.INTEGER, "level", "Niveau à atteindre pour la récompense", true)
                                .addOption(OptionType.ROLE, "role", "Rôle à donner lorsque le niveau est atteint", true),
                        new SubcommandData("remove", "Retirer une récompense")
                                .addOption(OptionType.INTEGER, "level", "Niveau auquel la récompense doit être supprimée", true, true),
                        new SubcommandData("list", "Affiche toutes les récompenses disponibles")
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "add" -> this.add(event);
            case "remove" -> this.remove(event);
            case "list" -> this.list(event);
        }
    }


    private void add(SlashCommandInteractionEvent event) {
        long level = event.getOption("level").getAsLong();
        Role role = event.getOption("role").getAsRole();

        try {
            guildRewardService.createReward(event.getGuild(), level, role);

            event.replyEmbeds(new EmbedBuilder()
                            .setTitle("\uD83C\uDFC6 Récompense ajoutée !")
                            .setDescription(STR."""
                                    Le rôle \{role.getAsMention()} a bien été ajouté comme récompense au **niveau \{level}**.
                                    """.trim())
                            .setColor(GravenColors.GREEN)
                            .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
        } catch (DataIntegrityViolationException _) {
            event.replyEmbeds(EmbedUtils
                            .error(STR."Il existe déjà une récompense associée au **niveau \{level}** !")
                            .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
        }
    }

    private void remove(SlashCommandInteractionEvent event) {
        long level = event.getOption("level").getAsLong();

        Optional<GravenGuildReward> reward = guildRewardService.getRewardForGuildAtLevel(event.getGuild(), level);

        if (reward.isEmpty()) {
            event.replyEmbeds(EmbedUtils
                            .error(STR."Il n'existe aucune récompense au niveau **\{level}** !")
                            .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
            return;
        }

        guildRewardService.delete(reward.get());
        String role = STR."**<@&\{reward.get().roleId()}>**";

        event.replyEmbeds(new EmbedBuilder()
                        .setTitle("❗ Récompense supprimée")
                        .setDescription(STR."Le rôle \{role} n'est plus une récompense au **niveau \{level}**.")
                        .setColor(GravenColors.GREEN)
                        .build())
                .addActionRow(DELETE_BUTTON)
                .queue();
    }

    private void list(SlashCommandInteractionEvent event) {
        List<GravenGuildReward> rewardsForGuild = guildRewardService.getRewardsForGuild(event.getGuild());

        StringBuilder rewards = new StringBuilder();

        for (GravenGuildReward reward : rewardsForGuild) {
            rewards.append(STR."Niveau **\{reward.level()}** : <@&\{reward.roleId()}>\n");
        }

        event.replyEmbeds(new EmbedBuilder()
                        .setTitle(STR."Récompenses sur \{event.getGuild().getName()}")
                        .setDescription(rewards.toString())
                        .setColor(GravenColors.PRIMARY)
                        .setAuthor(event.getGuild().getName(), null, event.getGuild().getIconUrl())
                        .build())
                .addActionRow(DELETE_BUTTON)
                .queue();
    }

    @Override
    public void autocomplete(CommandAutoCompleteInteractionEvent event) {
        System.out.println(event.getSubcommandName());
        if (event.getSubcommandName().equalsIgnoreCase("remove")) {
            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> list = guildRewardService.getRewardsForGuild(event.getGuild())
                    .stream()
                    .map(reward -> {
                        String name = event.getGuild().getRoleById(reward.roleId()).getName();
                        return new Choice(
                                STR."Niveau \{reward.level()} | Rôle : \{name}",
                                reward.level()
                        );
                    })
                    .toList();
            event.replyChoices(list).queue();

            return;
        }

        event.replyChoices().queue();
    }
}
