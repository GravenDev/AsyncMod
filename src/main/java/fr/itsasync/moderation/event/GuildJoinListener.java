package fr.itsasync.moderation.event;

import fr.itsasync.moderation.data.entity.AsyncMember;
import fr.itsasync.moderation.service.AsyncGuildRewardService;
import fr.itsasync.moderation.service.AsyncMemberService;
import fr.itsasync.moderation.util.annotation.Listener;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Listener
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter {

    private final AsyncMemberService memberService;
    private final AsyncGuildRewardService rewardService;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        AsyncMember gMember = memberService.getMemberByDiscordMember(member);
        rewardService.grantReward(member, gMember.level());
    }
}
