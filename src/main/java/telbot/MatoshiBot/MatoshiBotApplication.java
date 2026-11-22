package telbot.MatoshiBot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import telbot.MatoshiBot.bot.Bot;

@SpringBootApplication
public class MatoshiBotApplication implements CommandLineRunner {
	private final Bot bot;

	@Autowired
    public MatoshiBotApplication(Bot bot) {
        this.bot = bot;
    }


    public static void main(String[] args) {
		SpringApplication.run(MatoshiBotApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(bot);
	}
}
