package telbot.MatoshiBot.bot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {
    private final String botToken;
    private final String contextPrompt;
    private final ChatClient chatClient;

    @Autowired
    public Bot(@Value("${telegram.bot.api-key}") final String botToken,
               @Value("${ai.deepseek.prompt.system-prompt}") final String systemPrompt,
               @Value("${ai.deepseek.prompt.context-prompt}") final String contextPrompt,
               DeepSeekChatModel chatModel) {
        this.botToken = botToken;
        this.contextPrompt = contextPrompt;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        String userContent = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        System.out.printf("%s: [%s]\n", update.getMessage().getFrom().getUserName(), userContent);

        //Filter
        System.out.println(update.getMessage().getFrom().getUserName());

        if(userContent.equals("/start")) {
            sendText(chatId, "Поболтаем?");
        } else if(userContent.matches("\\b(Мат|Матоши|Матик|Матошик|Mat|Matoshi)\\b")) {
            String modelContent = this.chatClient.prompt()
                    .user(userContent)
                    .call()
                    .content();
            sendText(chatId, modelContent);
        } else {
            String modelContent = this.chatClient.prompt()
                    .user(String.format("%s. %s", userContent, contextPrompt))
                    .call()
                    .content();
            System.out.println(modelContent);
        }
    }

    public void sendText(Long chatId, String msg) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(msg)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "@matoshix_bot";
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
