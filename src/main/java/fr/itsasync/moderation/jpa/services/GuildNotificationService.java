package fr.itsasync.moderation.jpa.services;

import fr.itsasync.moderation.jpa.entities.AsyncGuildReward;
import fr.itsasync.moderation.jpa.entities.AsyncGuildSettings;
import fr.itsasync.moderation.jpa.entities.AsyncMember;
import fr.itsasync.moderation.utils.PlaceholderMessage;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class GuildNotificationService {

    private final AsyncGuildSettingsService settingsService;
    private final AsyncGuildRewardService rewardService;
    private final AsyncMemberService memberService;

    public Optional<Message> sendNotification(Member member, long level) {
        AsyncGuildSettings settings = settingsService.getOrCreateByGuild(member.getGuild());
        Optional<AsyncGuildReward> reward =
                rewardService.getRewardForGuildAtLevel(member.getGuild(), level);

        if (reward.isPresent() && !settings.rewardNotificationEnabled()) {
            reward = Optional.empty();
        }

        if (reward.isEmpty() && !settings.levelNotificationEnabled()) {
            return Optional.empty();
        }

        if (settings.dmNotifications()) {
            return sendDmNotification(member, settings, reward);
        }

        if (settings.notificationChannelId() >= 0) {
            return sendServerNotification(member, settings, reward);
        }

        return Optional.empty();
    }

    private Optional<Message> sendDmNotification(
            Member member, AsyncGuildSettings settings, Optional<AsyncGuildReward> reward) {
        AsyncMember gMember = memberService.getMemberByDiscordMember(member);

        PrivateChannel channel = member.getUser().openPrivateChannel().complete();

        String message =
                reward.map(r -> getRewardMessage(member, gMember, settings, r))
                        .orElseGet(() -> getMessage(member, gMember, settings));

        return Optional.of(
                channel.sendMessage(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .complete());
    }

    private Optional<Message> sendServerNotification(
            Member member, AsyncGuildSettings settings, Optional<AsyncGuildReward> reward) {
        AsyncMember gMember = memberService.getMemberByDiscordMember(member);

        MessageChannel channel =
                (MessageChannel)
                        member.getGuild()
                                .getGuildChannelById(
                                        settings.notificationChannelType(),
                                        settings.notificationChannelId());
        if (channel == null) {
            return Optional.empty();
        }

        String message =
                reward.map(r -> getRewardMessage(member, gMember, settings, r))
                        .orElseGet(() -> getMessage(member, gMember, settings));

        return Optional.of(
                channel.sendMessage(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .complete());
    }

    private String getMessage(Member member, AsyncMember gMember, AsyncGuildSettings settings) {
        return new PlaceholderMessage(settings.notificationMessage())
                .with("user.mention", member.getAsMention())
                .with("user.name", member.getEffectiveName())
                .with("user.id", member.getId())
                .with("level", String.valueOf(gMember.level()))
                .replace();
    }

    private String getRewardMessage(
            Member member,
            AsyncMember gMember,
            AsyncGuildSettings settings,
            AsyncGuildReward reward) {
        Role role = member.getGuild().getRoleById(reward.roleId());

        return new PlaceholderMessage(settings.rewardNotificationMessage())
                .with("user.mention", member.getAsMention())
                .with("user.name", member.getEffectiveName())
                .with("user.id", member.getId())
                .with("reward.mention", role == null ? "<@&1>" : role.getAsMention())
                .with("reward.name", role == null ? "undefined" : role.getName())
                .with("reward.id", role == null ? "undefined" : role.getId())
                .with("level", String.valueOf(gMember.level()))
                .replace();
    }
}
