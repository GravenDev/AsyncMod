package fr.redstom.gravenlevelling.jpa.services;

import fr.redstom.gravenlevelling.jpa.entities.GravenGuild;
import fr.redstom.gravenlevelling.jpa.GravenGuildReward;
import fr.redstom.gravenlevelling.jpa.GravenGuildRewardRepository;
import fr.redstom.gravenlevelling.jpa.entities.GravenMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GravenGuildRewardService {

    private final GravenGuildRewardRepository guildRewardRepository;

    private final GravenGuildService guildService;

    @Transactional
    public List<GravenGuildReward> getRewardsForGuild(Guild guild) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return guildRewardRepository.findAllByGuildOrderByLevelAsc(gGuild);
    }

    @Transactional
    public GravenGuildReward createReward(Guild guild, long level, Role roleToGive) throws DataIntegrityViolationException {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        GravenGuildReward reward = GravenGuildReward.builder()
                .guild(gGuild)
                .level(level)
                .roleId(roleToGive.getIdLong())
                .build();

        return guildRewardRepository.save(reward);
    }

    public Optional<GravenGuildReward> getRewardForGuildAtLevel(Guild guild, long level) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findByGuildAndLevel(gGuild, level);
    }

    public Optional<GravenGuildReward> getClosestRewardForGuildAtLevel(Guild guild, long level) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findTopByGuildAndLevelOrderByLevelDesc(gGuild, level);
    }

    public void grantReward(Member member, long level) {
        Optional<GravenGuildReward> oReward = getClosestRewardForGuildAtLevel(member.getGuild(), level);
        if (oReward.isEmpty()) {
            System.out.println(1);
            return;
        }

        GravenGuildReward reward = oReward.get();
        if (member.getRoles().stream().anyMatch(role -> role.getIdLong() == reward.roleId())) {
            System.out.println(2);
            return;
        }

        Role role = member.getGuild().getRoleById(reward.roleId());
        if (role == null) {
            System.out.println(3);
            return;
        }
        member.getGuild().addRoleToMember(member, role).queue(_ -> {
            System.out.println(STR."Granted \{role.getName()} to \{member.getEffectiveName()}");
        });
    }

    public void delete(GravenGuildReward reward) {
        guildRewardRepository.delete(reward);
    }
}
