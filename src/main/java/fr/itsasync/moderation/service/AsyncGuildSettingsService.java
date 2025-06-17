package fr.itsasync.moderation.service;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncGuildSettings;
import fr.itsasync.moderation.data.repository.AsyncGuildSettingsRepository;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Guild;

import org.springframework.stereotype.Service;

import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class AsyncGuildSettingsService {

    private final AsyncGuildService guildService;
    private final AsyncGuildSettingsRepository settingsRepository;

    public AsyncGuildSettings getOrCreateByGuild(Guild guild) {
        AsyncGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return settingsRepository
                .findByGuild(gGuild)
                .orElseGet(
                        () ->
                                settingsRepository.save(
                                        AsyncGuildSettings.builder().guild(gGuild).build()));
    }

    public AsyncGuildSettings applyAndSave(
            Guild guild, UnaryOperator<AsyncGuildSettings> settings) {
        AsyncGuildSettings gSettings = getOrCreateByGuild(guild);
        gSettings = settings.apply(gSettings);

        return settingsRepository.save(gSettings);
    }
}
