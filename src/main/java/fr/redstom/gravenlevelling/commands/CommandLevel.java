package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.services.GravenMemberService;
import fr.redstom.gravenlevelling.utils.Command;
import fr.redstom.gravenlevelling.utils.CommandExecutor;
import fr.redstom.gravenlevelling.utils.ImageGenerator;
import fr.redstom.gravenlevelling.utils.LevelUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;

@Command
@RequiredArgsConstructor
public class CommandLevel implements CommandExecutor {

    private final GravenMemberService memberService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("level", "Permet de voir votre niveau actuel sur le serveur")
                .addOption(OptionType.USER, "user", "Membre dont vous voulez savoir le niveau", false);
    }

    @SneakyThrows
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member discordMember = event.getOption("user", event.getMember(), OptionMapping::getAsMember);

        GravenMember member = memberService.getMemberByDiscordMember(discordMember);

        BufferedImage image = ImageGenerator.generateLevelImage(discordMember, member);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        stream.flush();

        event.reply(STR."You are level \{member.level()} and experience \{LevelUtils.formatExperience(member.experience(), member.level())}")
                .addFiles(FileUpload.fromData(stream.toByteArray(), "image.png"))
                .queue();
    }
}
