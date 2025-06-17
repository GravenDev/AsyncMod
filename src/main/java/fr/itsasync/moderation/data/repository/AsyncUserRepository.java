package fr.itsasync.moderation.data.repository;

import fr.itsasync.moderation.data.entity.AsyncUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsyncUserRepository extends CrudRepository<AsyncUser, Long> {}
