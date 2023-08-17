package co.com.jorge.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${config.base.path.server}")
    private String basePathServer;

    @Bean
    public WebClient registerWebClient(){
        return WebClient.create(basePathServer);
    }
}
