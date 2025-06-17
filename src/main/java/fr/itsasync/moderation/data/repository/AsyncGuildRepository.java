package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.AsyncGuild;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsyncGuildRepository extends CrudRepository<AsyncGuild, Long> {}
