package fr.redstom.gravenlevelling.jda.services;

import fr.redstom.gravenlevelling.jda.entities.GravenGuild;
import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.entities.GravenUser;
import fr.redstom.gravenlevelling.jda.repositories.GravenMemberRepository;
import fr.redstom.gravenlevelling.utils.LevelUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class GravenMemberService {

    private final LevelUtils levelUtils;

    private final GravenMemberRepository memberRepository;
    private final GravenGuildService guildService;
    private final GravenUserService userService;

    @Transactional
    public GravenMember getMemberByDiscordMember(Member member) {
        GravenUser user = userService.getOrCreateByDiscordUser(member.getUser());
        GravenGuild guild = guildService.getOrCreateByDiscordGuild(member.getGuild());

        return memberRepository
                .findByUserAndGuild(user, guild)
                .orElseGet(() -> memberRepository.save(GravenMember.builder()
                        .user(user)
                        .guild(guild)
                        .build()));
    }

    @Transactional
    public boolean addXp(Member member, Message message) {
        GravenMember gMember = getMemberByDiscordMember(member);
        Instant messageCreated = message.getTimeCreated().toInstant();

        long distance = ChronoUnit.MINUTES.between(messageCreated, gMember.lastMessageAt());
        if (Math.abs(distance) < 1) return false;

        long xpToGain = levelUtils.flattenMessageLengthIntoGain(message.getContentRaw().length());

        gMember.experience(gMember.experience() + xpToGain);
        gMember.lastMessageAt(messageCreated);

        memberRepository.save(gMember);

        checkLevel(member);

        return true;
    }

    @Transactional
    public boolean checkLevel(Member member) {
        GravenMember gMember = getMemberByDiscordMember(member);

        long xp = gMember.experience();
        long xpToNextLevel = levelUtils.xpForNextLevelAt(gMember.level());

        if (xp < xpToNextLevel) {
            return false;
        }

        gMember.experience(gMember.experience() % xpToNextLevel);
        gMember.level(gMember.level() + 1);

        memberRepository.save(gMember);

        return true;
    }

    @Transactional
    public int getRank(Member member) {
        GravenMember gMember = getMemberByDiscordMember(member);
        return memberRepository.findPositionOfMember(gMember.user(), gMember.guild());
    }
}
