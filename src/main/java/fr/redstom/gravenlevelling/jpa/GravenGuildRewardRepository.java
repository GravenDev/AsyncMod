package fr.redstom.gravenlevelling.jpa;

import fr.redstom.gravenlevelling.jpa.entities.GravenGuild;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GravenGuildRewardRepository extends CrudRepository<GravenGuildReward, Long> {

    List<GravenGuildReward> findAllByGuildOrderByLevelAsc(GravenGuild guild);

    Optional<GravenGuildReward> findTopByGuildAndLevelOrderByLevelDesc(GravenGuild guild, long level);
    Optional<GravenGuildReward> findByGuildAndLevel(GravenGuild guild, long level);

}
