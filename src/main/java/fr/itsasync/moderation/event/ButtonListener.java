package fr.itsasync.moderation.event;

import fr.itsasync.moderation.util.annotation.Listener;
import fr.itsasync.moderation.util.executor.ButtonExecutor;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;

@Listener
@RequiredArgsConstructor
public class ButtonListener extends ListenerAdapter {

    private final List<ButtonExecutor> buttonExecutors;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        buttonExecutors.stream()
                .filter(be -> be.id().equalsIgnoreCase(event.getButton().getId().split(";")[0]))
                .findFirst()
                .ifPresentOrElse(
                        be -> {
                            String[] args = event.getButton().getId().split(";");
                            args = Arrays.copyOfRange(args, 1, args.length);

                            be.execute(event, args);
                        },
                        () ->
                                event.reply(
                                                ":x: Impossible de trouver un exécuteur pour ce"
                                                        + " bouton !")
                                        .queue());
    }
}
