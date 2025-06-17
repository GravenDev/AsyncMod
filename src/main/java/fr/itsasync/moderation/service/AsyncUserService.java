package fr.itsasync.moderation.service;

import fr.itsasync.moderation.data.entity.AsyncUser;
import fr.itsasync.moderation.data.repository.AsyncUserRepository;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.User;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncUserService {

    private final AsyncUserRepository userRepository;

    public AsyncUser getOrCreateByDiscordUser(User user) {
        return userRepository
                .findById(user.getIdLong())
                .orElseGet(
                        () ->
                                userRepository.save(
                                        AsyncUser.builder().id(user.getIdLong()).build()));
    }

    public AsyncUser getOrCreateByUserId(long userId) {
        return userRepository
                .findById(userId)
                .orElseGet(() -> userRepository.save(AsyncUser.builder().id(userId).build()));
    }
}
