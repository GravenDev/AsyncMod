package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.utils.Command;
import fr.redstom.gravenlevelling.utils.CommandExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Command
public class CommandHelp implements CommandExecutor {

    @Override
    public SlashCommandData data() {
        return Commands.slash("level", "Permet de voir votre niveau actuel sur le serveur")
                .addOption(OptionType.USER, "user", "Membre dont vous voulez savoir le niveau", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Ok").queue();
    }
}
