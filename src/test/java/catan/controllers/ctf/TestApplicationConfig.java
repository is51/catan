package catan.controllers.ctf;

import catan.config.ApplicationConfig;
import catan.services.util.random.RandomValueGenerator;
import catan.services.util.random.RandomValueGeneratorMock;
import org.springframework.context.annotation.Bean;

public class TestApplicationConfig extends ApplicationConfig {

    @Bean
    protected RandomValueGenerator rvg() {
        return new RandomValueGeneratorMock();
    }
}