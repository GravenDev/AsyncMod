package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.GravenUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GravenUserRepository extends CrudRepository<GravenUser, Long> {}
