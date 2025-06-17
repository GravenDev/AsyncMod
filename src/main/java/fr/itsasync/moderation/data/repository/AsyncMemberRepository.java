package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncMember;
import fr.itsasync.moderation.data.entity.AsyncUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsyncMemberRepository
        extends CrudRepository<AsyncMember, AsyncMember.AsyncMemberId> {

    List<AsyncMember> findAllByUser(AsyncUser user);

    List<AsyncMember> findAllByUserId(long userId);

    List<AsyncMember> findAllByGuild(AsyncGuild guild);

    List<AsyncMember> findAllByGuildId(long guildId);

    @Query("select g from AsyncMember g where g.user = ?1 and g.guild = ?2")
    Optional<AsyncMember> findByUserAndGuild(AsyncUser user, AsyncGuild guild);

    @Query(
            """
    SELECT memberRank.rank
    FROM (
        SELECT gm.user as user,
               ROW_NUMBER() OVER (ORDER BY gm.level DESC, gm.experience DESC, gm.user.id ASC) AS rank
        FROM AsyncMember gm
        WHERE gm.guild = :guild
        AND NOT (gm.level = 0 AND gm.experience = 0)
    ) memberRank
    WHERE memberRank.user = :user
""")
    int findPositionOfMember(@Param("user") AsyncUser user, @Param("guild") AsyncGuild guild);

    @Query(
            """
            SELECT g
            FROM AsyncMember g
            WHERE
                g.guild = ?1
                AND NOT (g.level = 0 AND g.experience = 0)
            ORDER BY
                g.level DESC,
                g.experience DESC,
                g.user.id ASC
            """)
    Page<AsyncMember> findAllByGuild(AsyncGuild guild, Pageable config);
}
