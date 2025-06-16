package fr.itsasync.moderation.jpa.repositories;

import fr.itsasync.moderation.jpa.entities.AsyncGuild;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsyncGuildRepository extends CrudRepository<AsyncGuild, Long> {}
