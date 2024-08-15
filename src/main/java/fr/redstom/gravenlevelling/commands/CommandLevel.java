package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.services.GravenMemberService;
import fr.redstom.gravenlevelling.utils.Command;
import fr.redstom.gravenlevelling.utils.CommandExecutor;
import fr.redstom.gravenlevelling.utils.ImageGenerator;
import fr.redstom.gravenlevelling.utils.LevelUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;

@Command
@RequiredArgsConstructor
public class CommandLevel implements CommandExecutor {

    private final GravenMemberService memberService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("level", "Permet de voir votre niveau actuel sur le serveur")
                .addOption(OptionType.USER, "user", "Membre dont vous voulez savoir le niveau", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        assert event.getMember() != null;

        GravenMember member = memberService.getMemberByDiscordMember(event.getMember());

        event.reply(STR."You are level \{member.level()} and experience \{member.experience()}/\{LevelUtils.xpForNextLevelAt(member.level())}").queue();
    }
}
