package telbot.MatoshiBot.config;

import org.springframework.ai.session.DefaultSessionService;
import org.springframework.ai.session.InMemorySessionRepository;
import org.springframework.ai.session.SessionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public SessionService sessionService() {
        return DefaultSessionService.builder()
                .sessionRepository(InMemorySessionRepository.builder().build())
                .build();
    }
}
