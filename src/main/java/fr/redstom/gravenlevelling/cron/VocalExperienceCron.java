package fr.redstom.gravenlevelling.cron;

import fr.redstom.gravenlevelling.jpa.services.GravenMemberService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VocalExperienceCron {

    private final GravenMemberService memberService;
    private final JDA bot;

    @Value("${xp.per_30sec}")
    private int per30sec = 1;

    @Scheduled(cron = "*/30 * * * * *")
    public void compute() {
        for (Guild guild : bot.getGuilds()) {
            for (VoiceChannel channel : guild.getVoiceChannels()) {
                channel.getMembers().stream()
                        .filter(user -> user.getVoiceState() != null)
                        .filter(user -> !user.getVoiceState().isDeafened())
                        .filter(user -> !user.getVoiceState().isMuted())
                        .forEach(member -> memberService.addXp(member, per30sec));
            }

        }
    }

}
