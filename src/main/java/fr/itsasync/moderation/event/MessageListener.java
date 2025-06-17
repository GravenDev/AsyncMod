package fr.itsasync.moderation.event;

import fr.itsasync.moderation.service.AsyncMemberService;

import fr.itsasync.moderation.util.annotation.Listener;
import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Listener
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
