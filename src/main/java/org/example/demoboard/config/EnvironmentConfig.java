package org.example.demoboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EnvironmentConfig {

    private final Environment env;

    public EnvironmentConfig(Environment env) {
        this.env = env;
    }

//    @Bean
//    public Environment environment() {
//        return env;
//    }
}