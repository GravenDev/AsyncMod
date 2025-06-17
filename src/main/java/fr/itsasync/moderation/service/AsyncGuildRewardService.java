package fr.itsasync.moderation.service;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncGuildReward;
import fr.itsasync.moderation.data.repository.AsyncGuildRewardRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncGuildRewardService {

    private final AsyncGuildRewardRepository guildRewardRepository;

    private final AsyncGuildService guildService;

    @Transactional
    public List<AsyncGuildReward> getRewardsForGuild(Guild guild) {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return guildRewardRepository.findAllByGuildOrderByLevelAsc(gGuild);
    }

    @Transactional
    public AsyncGuildReward createReward(Guild guild, long level, Role roleToGive)
            throws DataIntegrityViolationException {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        AsyncGuildReward reward =
                AsyncGuildReward.builder()
                        .guild(gGuild)
                        .level(level)
                        .roleId(roleToGive.getIdLong())
                        .build();

        return guildRewardRepository.save(reward);
    }

    public Optional<AsyncGuildReward> getRewardForGuildAtLevel(Guild guild, long level) {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findByGuildAndLevel(gGuild, level);
    }

    public Optional<AsyncGuildReward> getClosestRewardForGuildAtLevel(Guild guild, long level) {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findTopByGuildAndLevelOrderByLevelDesc(gGuild, level);
    }

    public void grantReward(Member member, long level) {
        Optional<AsyncGuildReward> oReward =
                getClosestRewardForGuildAtLevel(member.getGuild(), level);
        if (oReward.isEmpty()) {
            return;
        }

        AsyncGuildReward reward = oReward.get();
        if (member.getRoles().stream().anyMatch(role -> role.getIdLong() == reward.roleId())) {
            return;
        }

        Role role = member.getGuild().getRoleById(reward.roleId());
        if (role == null) {
            return;
        }

        member.getGuild().addRoleToMember(member, role).queue();
        log.info(
                "{} has gained reward {} in guild {}",
                member.getUser().getAsTag(),
                role.getName(),
                role.getGuild().getName());
    }

    public Optional<AsyncGuildReward> getByMemberRole(Member member, Role role) {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(member.getGuild());
        return guildRewardRepository.findByGuildAndRoleId(gGuild, role.getIdLong());
    }

    public void delete(AsyncGuildReward reward) {
        guildRewardRepository.delete(reward);
    }
}
