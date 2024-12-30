package fr.redstom.gravenlevelling.cron;

import fr.redstom.gravenlevelling.jpa.services.GravenMemberService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VocalExperienceCron {

    private final GravenMemberService memberService;
    private final JDA bot;

    @Scheduled(cron = "*/30 * * * * *")
    public void compute() {
        System.out.println("Cron compute");

        for (Guild guild : bot.getGuilds()) {
            System.out.println("Guild: " + guild.getName());
            for (VoiceChannel channel : guild.getVoiceChannels()) {
                System.out.println("VoiceChannel: " + channel.getName());
                channel.getMembers().stream()
                        .filter(user -> user.getVoiceState() != null)
                        .filter(user -> !user.getVoiceState().isDeafened())
                        .filter(user -> !user.getVoiceState().isMuted())
                        .peek(member -> System.out.println("User: " + member.getUser().getName()))
                        .forEach(member -> memberService.addXp(member, 1));
            }

        }
    }

}
