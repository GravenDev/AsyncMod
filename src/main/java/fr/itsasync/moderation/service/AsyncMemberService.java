package fr.itsasync.moderation.service;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncGuildSettings;
import fr.itsasync.moderation.data.entity.AsyncMember;
import fr.itsasync.moderation.data.entity.AsyncUser;
import fr.itsasync.moderation.data.repository.AsyncMemberRepository;
import fr.itsasync.moderation.util.LevelUtils;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class AsyncMemberService {

    private final LevelUtils levelUtils;

    private final AsyncMemberRepository memberRepository;

    private final AsyncGuildService guildService;
    private final AsyncUserService userService;
    private final AsyncGuildRewardService rewardService;
    private final GuildNotificationService notificationService;
    private final AsyncGuildSettingsService settingsService;

    private final JDA jda;

    @Value("${xp.timeout}")
    private int timeout = 60;

    @Transactional
    public AsyncMember getMemberByDiscordMember(Member member) {
        AsyncUser user = userService.getOrCreateByDiscordUser(member.getUser());
        AsyncGuild guild = guildService.getOrCreateByDiscordGuild(member.getGuild());

        return memberRepository
                .findByUserAndGuild(user, guild)
                .orElseGet(() -> this.createMember(member, user, guild));
    }

    @Transactional
    public AsyncMember getMemberByGuildAndMemberId(Guild guild, long userId, long baseLevel) {
        AsyncUser user = userService.getOrCreateByUserId(userId);
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return memberRepository
                .findByUserAndGuild(user, gGuild)
                .orElseGet(
                        () ->
                                memberRepository.save(
                                        AsyncMember.builder()
                                                .user(user)
                                                .guild(gGuild)
                                                .level(baseLevel)
                                                .build()));
    }

    private AsyncMember createMember(Member member, AsyncUser user, AsyncGuild guild) {
        AsyncGuildSettings settings = settingsService.getOrCreateByGuild(member.getGuild());

        AtomicLong level = new AtomicLong();
        if (settings.autoLevelGrant()) {
            for (Role role : member.getRoles()) {
                rewardService
                        .getByMemberRole(member, role)
                        .ifPresent(
                                reward -> {
                                    if (reward.level() > level.get()) {
                                        level.set(reward.level());
                                    }
                                });
            }
        }

        return memberRepository.save(
                AsyncMember.builder().user(user).guild(guild).level(level.get()).build());
    }

    @Transactional
    public boolean addXpFromMessage(Member member, Message message) {
        if (settingsService.getOrCreateByGuild(member.getGuild()).pause()) {
            return false;
        }

        AsyncMember gMember = getMemberByDiscordMember(member);

        Instant messageCreated = message.getTimeCreated().toInstant();

        long distance = ChronoUnit.SECONDS.between(messageCreated, gMember.lastMessageAt());
        if (Math.abs(distance) < timeout) return false;

        long xpToGain = levelUtils.flattenMessageLengthIntoGain(message.getContentRaw().length());

        gMember.experience(gMember.experience() + xpToGain);
        gMember.lastMessageAt(messageCreated);

        checkLevel(member);
        memberRepository.save(gMember);

        log.info("{} got added {} xp from message.", member.getUser().getAsTag(), xpToGain);
        return true;
    }

    @Transactional
    public void addXp(Member member, long amount, String reason) {
        if (settingsService.getOrCreateByGuild(member.getGuild()).pause()) {
            return;
        }

        AsyncMember gMember = getMemberByDiscordMember(member);

        gMember.experience(gMember.experience() + amount);
        memberRepository.save(gMember);
        log.info(
                "{} got added {} xp for {} in guild {}.",
                member.getUser().getAsTag(),
                amount,
                reason,
                member.getGuild().getName());

        checkLevel(member);
    }

    @Transactional
    public void addXp(Member member, long amount) {
        addXp(member, amount, "Unknown reason");
    }

    @Transactional
    public boolean checkLevel(Member member) {
        AsyncMember gMember = getMemberByDiscordMember(member);

        long xp = gMember.experience();
        long xpToNextLevel = levelUtils.xpForNextLevelAt(gMember.level());

        if (xp < xpToNextLevel) {
            return false;
        }

        do {
            gMember.experience(gMember.experience() - xpToNextLevel);
            gMember.level(gMember.level() + 1);

            xpToNextLevel = levelUtils.xpForNextLevelAt(gMember.level());
        } while (gMember.experience() > xpToNextLevel);

        rewardService.grantReward(member, gMember.level());
        notificationService.sendNotification(member, gMember.level());

        memberRepository.save(gMember);
        log.info(
                "{} has levelled up to level {} in guild {}.",
                member.getUser().getAsTag(),
                gMember.level(),
                gMember.level());

        return true;
    }

    @Transactional
    public int getRank(Member member) {
        AsyncMember gMember = getMemberByDiscordMember(member);
        return memberRepository.findPositionOfMember(gMember.user(), gMember.guild());
    }

    @Nullable
    public Member getDiscordMemberByMember(AsyncMember asyncMember) {
        Guild guild = jda.getGuildById(asyncMember.guild().id());
        Member member = guild.getMemberById(asyncMember.user().id());

        if (member == null) {
            try {
                member =
                        guild.retrieveMemberById(asyncMember.user().id()).useCache(true).complete();
            } catch (Exception _) {
                return null;
            }
        }

        return member;
    }
}
