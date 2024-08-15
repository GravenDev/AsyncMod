package fr.redstom.gravenlevelling.jda.repositories;

import fr.redstom.gravenlevelling.jda.entities.GravenUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GravenUserRepository extends CrudRepository<GravenUser, Long> {

}
