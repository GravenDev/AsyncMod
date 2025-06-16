package fr.itsasync.moderation.events;

import fr.itsasync.moderation.jpa.services.AsyncMemberService;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {

    private final AsyncMemberService memberService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()
                || event.getAuthor().isSystem()
                || event.isWebhookMessage()
                || event.getMember() == null) return;

        memberService.addXpFromMessage(event.getMember(), event.getMessage());
    }
}
