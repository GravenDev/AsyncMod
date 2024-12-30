package fr.redstom.gravenlevelling.jpa.repositories;

import fr.redstom.gravenlevelling.jpa.entities.GravenUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GravenUserRepository extends CrudRepository<GravenUser, Long> {}
