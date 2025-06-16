package fr.itsasync.moderation.jpa.repositories;

import fr.itsasync.moderation.jpa.entities.GravenUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GravenUserRepository extends CrudRepository<GravenUser, Long> {}
