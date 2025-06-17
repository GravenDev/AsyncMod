package fr.itsasync.moderation.cron;

import fr.itsasync.moderation.service.AsyncMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class VocalExperienceCron {

    private final AsyncMemberService memberService;
    private final JDA bot;

    @Value("${xp.per_30sec}")
    private int per30sec = 1;

    @Scheduled(cron = "*/30 * * * * *")
    public void compute() {
        log.info("Starting xp experience cron");

        AtomicLong counter = new AtomicLong();
        for (Guild guild : bot.getGuilds()) {
            for (VoiceChannel channel : guild.getVoiceChannels()) {

                List<Member> members =
                        channel.getMembers().stream()
                                .filter(user -> user.getVoiceState() != null)
                                .filter(user -> !user.getVoiceState().isDeafened())
                                .filter(user -> !user.getVoiceState().isMuted())
                                .toList();

                counter.addAndGet(members.size());

                if (members.size() < 2) {
                    continue;
                }

                members.forEach(member -> memberService.addXp(member, per30sec, "Voice activity"));
            }
        }

        log.info("Xp experience cron complete and {} users updated", counter.get());
    }
}
