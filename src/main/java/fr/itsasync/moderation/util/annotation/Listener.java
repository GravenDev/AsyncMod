package fr.itsasync.moderation.util.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Service
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
}
