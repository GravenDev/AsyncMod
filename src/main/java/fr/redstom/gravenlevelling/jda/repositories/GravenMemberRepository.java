package fr.redstom.gravenlevelling.jda.repositories;

import fr.redstom.gravenlevelling.jda.entities.GravenGuild;
import fr.redstom.gravenlevelling.jda.entities.GravenMember;
import fr.redstom.gravenlevelling.jda.entities.GravenUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GravenMemberRepository extends CrudRepository<GravenMember, GravenMember.GravenMemberId> {

    List<GravenMember> findAllByUser(GravenUser user);
    List<GravenMember> findAllByUserId(long userId);

    List<GravenMember> findAllByGuild(GravenGuild guild);
    List<GravenMember> findAllByGuildId(long guildId);

    Optional<GravenMember> findByUserAndGuild(GravenUser user, GravenGuild guild);
}
