package fr.redstom.gravenlevelling.config;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;
import java.util.List;

@Configuration
@Slf4j
public class JDAConfig {

    @Bean
    JDA client(@Value("${bot.token}") String token, List<ListenerAdapter> eventListeners) throws InterruptedException {
        JDA client = JDABuilder.create(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)).build();

        client.addEventListener(eventListeners.toArray());

        return client.awaitReady();
    }

}
