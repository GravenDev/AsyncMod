package fr.itsasync.moderation.jpa.repositories;

import fr.itsasync.moderation.jpa.entities.AsyncGuild;
import fr.itsasync.moderation.jpa.entities.AsyncGuildSettings;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AsyncGuildSettingsRepository extends CrudRepository<AsyncGuildSettings, Long> {

    Optional<AsyncGuildSettings> findByGuild(AsyncGuild gGuild);
}
