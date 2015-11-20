package catan.controllers.ctf;

import catan.config.ApplicationConfig;
import catan.services.util.random.RandomUtil;
import catan.services.util.random.RandomUtilMock;
import org.springframework.context.annotation.Bean;

public class TestApplicationConfig extends ApplicationConfig {

    @Bean
    protected RandomUtil randomUtil() {
        return new RandomUtilMock();
    }
}