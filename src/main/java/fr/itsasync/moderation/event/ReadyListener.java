package fr.itsasync.moderation.event;

import fr.itsasync.moderation.util.annotation.Listener;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Listener
@Slf4j
public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getPresence().setActivity(Activity.listening("tes messages 👁️👄👁️"));
    }
}
