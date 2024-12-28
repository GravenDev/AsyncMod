package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jpa.entities.GravenMember;
import fr.redstom.gravenlevelling.jpa.repositories.GravenMemberRepository;
import fr.redstom.gravenlevelling.jpa.services.GravenMemberService;
import fr.redstom.gravenlevelling.utils.jda.Command;
import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@Command
@RequiredArgsConstructor
public class CommandLevels implements CommandExecutor {

    private final GravenMemberService memberService;
    private final GravenMemberRepository memberRepository;

    @Override
    public SlashCommandData data() {
        return Commands.slash("levels", "Permet de gérer les niveaux des membres")
                .addSubcommands(
                        new SubcommandData("add", "Ajoute des niveaux à un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur")
                                .addOption(OptionType.INTEGER, "lvl", "Niveaux à ajouter"),
                        new SubcommandData("remove", "Retire des niveaux à un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur")
                                .addOption(OptionType.INTEGER, "lvl", "Niveaux à retirer"),
                        new SubcommandData("set", "Définit le niveau d'un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur")
                                .addOption(OptionType.INTEGER, "lvl", "Niveau à définir")
                )
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "add" -> this.add(event);
            case "remove" -> this.remove(event);
            case "set" -> this.set(event);
        }
    }

    private void add(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        gMember.level(gMember.level() + lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(STR."✅ L'utilisateur \{member.getAsMention()} est maintenant au niveau **\{gMember.level()}**").queue();
    }

    private void remove(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        gMember.level(gMember.level() - lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(STR."✅ L'utilisateur \{member.getAsMention()} est maintenant au niveau **\{gMember.level()}**").queue();
    }

    private void set(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        gMember.level(lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(STR."✅ L'utilisateur \{member.getAsMention()} est maintenant au niveau **\{gMember.level()}**").queue();
    }
}
