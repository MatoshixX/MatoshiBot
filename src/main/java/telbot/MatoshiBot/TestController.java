package telbot.MatoshiBot;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    public ChatClient chatClient;

    public TestController(DeepSeekChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping("/ai/chat/prompt")
    public String get(@RequestParam("prompt") String prompt) {
        return this.chatClient.prompt().user(prompt).call().content();
    }
}

