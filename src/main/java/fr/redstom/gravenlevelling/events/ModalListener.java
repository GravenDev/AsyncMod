package fr.redstom.gravenlevelling.events;

import fr.redstom.gravenlevelling.utils.jda.ModalHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModalListener extends ListenerAdapter implements BeanPostProcessor {

    Map<String, Consumer<ModalInteractionEvent>> handlers = new HashMap<>();

    /**
     * Loads all methods annotated with {@link ModalHandler} and stores them in a map
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Method method : bean.getClass().getMethods()) {
            if (method.isAnnotationPresent(ModalHandler.class)) {
                if(method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(ModalInteractionEvent.class)) {
                    throw new IllegalArgumentException("Method annotated with @ModalHandler must have a single parameter of type ModalInteractionEvent");
                }

                ModalHandler annotation = method.getAnnotation(ModalHandler.class);

                handlers.put(annotation.value(), event -> {
                    try {
                        method.invoke(bean, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return bean;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        handlers.getOrDefault(event.getModalId(), _ -> {}).accept(event);
    }
}
