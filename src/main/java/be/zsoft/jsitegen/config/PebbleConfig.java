package be.zsoft.jsitegen.config;

import io.pebbletemplates.pebble.PebbleEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PebbleConfig {

    @Bean
    public PebbleEngine pebbleEngine() {
        return new PebbleEngine.Builder()
                .build();
    }
}
