package fr.redstom.gravenlevelling;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication()
public class GravenLevellingApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GravenLevellingApplication.class)
                .build()
                .run(args);
    }

}
