package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncGuildSettings;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsyncGuildSettingsRepository extends CrudRepository<AsyncGuildSettings, Long> {

    Optional<AsyncGuildSettings> findByGuild(AsyncGuild gGuild);
}
