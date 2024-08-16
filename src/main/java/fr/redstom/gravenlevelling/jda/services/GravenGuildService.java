package fr.redstom.gravenlevelling.jda.services;

import fr.redstom.gravenlevelling.jda.entities.GravenGuild;
import fr.redstom.gravenlevelling.jda.repositories.GravenGuildRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GravenGuildService {

    private final GravenGuildRepository guildRepository;

    public GravenGuild getOrCreateByDiscordGuild(Guild guild) {
        return guildRepository
                .findById(guild.getIdLong())
                .orElseGet(() -> guildRepository.save(
                        GravenGuild.builder()
                                .id(guild.getIdLong())
                                .build()));
    }
}
