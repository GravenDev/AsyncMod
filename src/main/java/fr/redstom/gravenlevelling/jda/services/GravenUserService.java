package fr.redstom.gravenlevelling.jda.services;

import fr.redstom.gravenlevelling.jda.entities.GravenUser;
import fr.redstom.gravenlevelling.jda.repositories.GravenUserRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GravenUserService {

    private final GravenUserRepository userRepository;

    public GravenUser getOrCreateByDiscordUser(User user) {
        return userRepository
                .findById(user.getIdLong())
                .orElseGet(() -> {
                    System.out.println("Creating new user");
                    return userRepository.save(
                            GravenUser.builder()
                                    .id(user.getIdLong())
                                    .build());
                });
    }

}
