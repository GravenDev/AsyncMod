package fr.itsasync.moderation.util.executor;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface ButtonExecutor {

    String id();

    void execute(ButtonInteractionEvent event, String[] args);
}
