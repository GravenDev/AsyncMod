package fr.itsasync.moderation.events;

import fr.itsasync.moderation.jpa.entities.AsyncMember;
import fr.itsasync.moderation.jpa.services.AsyncGuildRewardService;
import fr.itsasync.moderation.jpa.services.AsyncMemberService;
import fr.itsasync.moderation.utils.jda.Listener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Service;

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
