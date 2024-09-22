package fr.redstom.gravenlevelling.jpa.repositories;

import fr.redstom.gravenlevelling.jpa.entities.GravenGuild;
import fr.redstom.gravenlevelling.jpa.entities.GravenGuildReward;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GravenGuildRewardRepository extends CrudRepository<GravenGuildReward, Long> {

    List<GravenGuildReward> findAllByGuildOrderByLevelAsc(GravenGuild guild);

    Optional<GravenGuildReward> findTopByGuildAndLevelOrderByLevelDesc(GravenGuild guild, long level);
    Optional<GravenGuildReward> findByGuildAndLevel(GravenGuild guild, long level);

    Optional<GravenGuildReward> findByGuildAndRoleId(GravenGuild guild, long roleId);

}
