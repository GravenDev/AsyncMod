package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncGuildReward;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsyncGuildRewardRepository extends CrudRepository<AsyncGuildReward, Long> {

    List<AsyncGuildReward> findAllByGuildOrderByLevelAsc(AsyncGuild guild);

    Optional<AsyncGuildReward> findTopByGuildAndLevelOrderByLevelDesc(AsyncGuild guild, long level);

    Optional<AsyncGuildReward> findByGuildAndLevel(AsyncGuild guild, long level);

    Optional<AsyncGuildReward> findByGuildAndRoleId(AsyncGuild guild, long roleId);
}
