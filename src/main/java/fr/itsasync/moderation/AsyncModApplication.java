package fr.itsasync.moderation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AsyncModApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(AsyncModApplication.class).build().run(args);
    }
}
