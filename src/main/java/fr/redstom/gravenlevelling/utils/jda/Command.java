package fr.redstom.gravenlevelling.utils.jda;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.stereotype.Service;

@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
}
