package fr.itsasync.moderation.component.command;

import static fr.itsasync.moderation.component.button.DeleteButton.DELETE_BUTTON;

import fr.itsasync.moderation.data.entity.AsyncGuildReward;
import fr.itsasync.moderation.service.AsyncGuildRewardService;
import fr.itsasync.moderation.util.AsyncColors;
import fr.itsasync.moderation.util.annotation.Command;
import fr.itsasync.moderation.util.executor.CommandExecutor;
import fr.itsasync.moderation.util.Embeds;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
@Command
@RequiredArgsConstructor
public class CommandReward implements CommandExecutor {

    private final AsyncGuildRewardService guildRewardService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("rewards", "Configurer les récompenses de niveau")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(
                        new SubcommandData("add", "Ajouter une récompense")
                                .addOption(
                                        OptionType.INTEGER,
                                        "level",
                                        "Niveau à atteindre pour la récompense",
                                        true)
                                .addOption(
                                        OptionType.ROLE,
                                        "role",
                                        "Rôle à donner lorsque le niveau est atteint",
                                        true),
                        new SubcommandData("remove", "Retirer une récompense")
                                .addOption(
                                        OptionType.INTEGER,
                                        "level",
                                        "Niveau auquel la récompense doit être supprimée",
                                        true,
                                        true),
                        new SubcommandData("list", "Affiche toutes les récompenses disponibles"));
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

            event.replyEmbeds(
                            new EmbedBuilder()
                                    .setTitle("\uD83C\uDFC6 Récompense ajoutée !")
                                    .setDescription(
                                            ("Le rôle "
                                                            + role.getAsMention()
                                                            + " a bien été ajouté comme récompense"
                                                            + " au **niveau "
                                                            + level
                                                            + "**.\n")
                                                    .trim())
                                    .setColor(AsyncColors.GREEN)
                                    .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
            log.info(
                    "{} added a reward at level {} with role {} in guild {}",
                    event.getMember().getUser().getAsTag(),
                    level,
                    role.getName(),
                    event.getGuild().getName());
        } catch (DataIntegrityViolationException _) {
            event.replyEmbeds(
                            Embeds.error(
                                            "Il existe déjà une récompense associée au **niveau "
                                                    + level
                                                    + "** !")
                                    .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
        }
    }

    private void remove(SlashCommandInteractionEvent event) {
        long level = event.getOption("level").getAsLong();

        Optional<AsyncGuildReward> reward =
                guildRewardService.getRewardForGuildAtLevel(event.getGuild(), level);

        if (reward.isEmpty()) {
            event.replyEmbeds(
                            Embeds.error(
                                            "Il n'existe aucune récompense au niveau **"
                                                    + level
                                                    + "** !")
                                    .build())
                    .addActionRow(DELETE_BUTTON)
                    .queue();
            return;
        }

        guildRewardService.delete(reward.get());
        String role = "**<@&" + reward.get().roleId() + ">**";

        event.replyEmbeds(
                        new EmbedBuilder()
                                .setTitle("❗ Récompense supprimée")
                                .setDescription(
                                        "Le rôle "
                                                + role
                                                + " n'est plus une récompense au **niveau "
                                                + level
                                                + "**.")
                                .setColor(AsyncColors.GREEN)
                                .build())
                .addActionRow(DELETE_BUTTON)
                .queue();
        log.info(
                "{} removed reward at level {} in guild {}",
                event.getMember().getUser().getAsTag(),
                level,
                event.getGuild().getName());
    }

    private void list(SlashCommandInteractionEvent event) {
        List<AsyncGuildReward> rewardsForGuild =
                guildRewardService.getRewardsForGuild(event.getGuild());

        StringBuilder rewards = new StringBuilder();

        for (AsyncGuildReward reward : rewardsForGuild) {
            rewards.append("Niveau **" + reward.level() + "** : <@&" + reward.roleId() + ">\n");
        }

        event.replyEmbeds(
                        new EmbedBuilder()
                                .setTitle("Récompenses sur " + event.getGuild().getName())
                                .setDescription(rewards.toString())
                                .setColor(AsyncColors.PRIMARY)
                                .setAuthor(
                                        event.getGuild().getName(),
                                        null,
                                        event.getGuild().getIconUrl())
                                .build())
                .addActionRow(DELETE_BUTTON)
                .queue();
    }

    @Override
    public void autocomplete(CommandAutoCompleteInteractionEvent event) {
        if (event.getSubcommandName().equalsIgnoreCase("remove")) {
            List<net.dv8tion.jda.api.interactions.commands.Command.Choice> list =
                    guildRewardService.getRewardsForGuild(event.getGuild()).stream()
                            .map(
                                    reward -> {
                                        String name =
                                                event.getGuild()
                                                        .getRoleById(reward.roleId())
                                                        .getName();
                                        return new Choice(
                                                "Niveau " + reward.level() + " | Rôle : " + name,
                                                reward.level());
                                    })
                            .toList();
            event.replyChoices(list).queue();

            return;
        }

        event.replyChoices().queue();
    }
}
