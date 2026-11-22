package telbot.MatoshiBot.bot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.session.EventFilter;
import org.springframework.ai.session.SessionService;
import org.springframework.ai.session.advisor.SessionAdvisors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telbot.MatoshiBot.filter.UserFilter;

import java.util.regex.Pattern;

@Component
public class Bot extends TelegramLongPollingBot {
    private final String botToken;
    private final String contextPrompt;
    private final ChatClient chatClient;

    @Autowired
    public Bot(@Value("${telegram.bot.api-key}") final String botToken,
               @Value("${ai.deepseek.prompt.system-prompt}") final String systemPrompt,
               @Value("${ai.deepseek.prompt.context-prompt}") final String contextPrompt,
               DeepSeekChatModel chatModel,
               SessionService sessionService) {
        this.botToken = botToken;
        this.contextPrompt = contextPrompt;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        SessionAdvisors.builder(sessionService)
                                .memory(memory -> memory.eventFilter(EventFilter.lastN(10)))
                                .build()
                )
                .build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message;
        if (update.hasMessage()) {
            message = update.getMessage();
        } else if (update.hasChannelPost()) {
            message = update.getChannelPost();
        } else {
            return;
        }

        if (message.getFrom() != null) {
            System.out.println(message.getFrom().getUserName());
        } else if (message.getSenderChat() != null) {
            System.out.println(message.getSenderChat().getTitle());
        }

        String userContent = message.getText();
        Long chatId = message.getChatId();
        System.out.printf("%s: [%s]\n", message.getFrom().getUserName(), userContent);
        //if(UserFilter.isAllow(message.getFrom().getUserName())) {
            if(userContent.equals("/start")) {
                sendText(chatId, "Поболтаем?");
            } else if(Pattern.compile(
                    "(?<![\\p{L}\\p{N}_])(Мат|Матоши|Матик|Матошик|Mat|Matoshi)(?![\\p{L}\\p{N}_])",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
            ).matcher(userContent).find()) {
                String modelContent = this.chatClient.prompt()
                        .user(userContent)
                        .advisors(SessionAdvisors.session(chatId.toString()))
                        .call()
                        .content();
                sendText(chatId, modelContent);
            } else {
                this.chatClient.prompt()
                        .user(String.format("%s. %s", userContent, contextPrompt))
                        .advisors(SessionAdvisors.session(chatId.toString()))
                        .call()
                        .content();
            }
        //} else {
        //    sendText(chatId, "У вас нет доступа к этому боту.");
        //}
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
