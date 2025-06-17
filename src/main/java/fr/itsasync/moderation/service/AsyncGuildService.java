package fr.itsasync.moderation.service;

import fr.itsasync.moderation.data.entity.AsyncGuild;
import fr.itsasync.moderation.data.entity.AsyncMember;
import fr.itsasync.moderation.data.repository.AsyncGuildRepository;
import fr.itsasync.moderation.data.repository.AsyncMemberRepository;
import fr.itsasync.moderation.util.ImageGenerator;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class AsyncGuildService {

    private final AsyncGuildRepository guildRepository;
    private final AsyncMemberRepository memberRepository;

    private final AsyncMemberService memberService;

    private final ImageGenerator imageGenerator;

    public AsyncGuild getOrCreateByDiscordGuild(Guild guild) {
        return guildRepository
                .findById(guild.getIdLong())
                .orElseGet(
                        () ->
                                guildRepository.save(
                                        AsyncGuild.builder().id(guild.getIdLong()).build()));
    }

    @Transactional
    public Page<AsyncMember> getLeaderboardOf(Guild guild, int page) {
        AsyncGuild gGuild = getOrCreateByDiscordGuild(guild);

        return memberRepository.findAllByGuild(gGuild, PageRequest.of(page - 1, 10));
    }

    @Transactional
    @SneakyThrows
    public byte[] getLeaderboardImageFor(Guild guild, int page, @Nullable Member member) {
        Page<AsyncMember> members = getLeaderboardOf(guild, page);
        if (members.isEmpty()) {
            return null;
        }

        AsyncMember gMember =
                member == null ? null : memberService.getMemberByDiscordMember(member);
        BufferedImage image =
                imageGenerator.generateLeaderboardImage(
                        page,
                        gMember,
                        members.getContent(),
                        memberService::getDiscordMemberByMember);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        stream.flush();

        return stream.toByteArray();
    }
}
