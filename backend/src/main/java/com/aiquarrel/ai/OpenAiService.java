package com.aiquarrel.ai;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatCompletion;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ChatCompletionMessageParam;
import com.openai.models.ChatCompletionSystemMessageParam;
import com.openai.models.ChatCompletionUserMessageParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    private final OpenAIClient client;
    private final PromptBuilder promptBuilder;

    @Value("${ai.deepseek.model}")
    private String model;

    @Value("${ai.deepseek.max-tokens:200}")
    private int maxTokens;

    @Value("${ai.deepseek.temperature:1.2}")
    private double temperature;

    /**
     * 调用 DeepSeek API 生成怼人话术。
     */
    public String generateQuarrel(String scene, String style) {
        String systemPrompt = promptBuilder.getSystemPrompt();
        String userPrompt = promptBuilder.buildUserPrompt(scene, style);
        ChatCompletionCreateParams params = buildCompletionParams(systemPrompt, userPrompt);

        log.info("调用DeepSeek API: scene={}, style={}", scene, style);
        ChatCompletion completion = client.chat().completions().create(params);

        String content = completion.choices().get(0).message().content()
                .orElse("这个问题有点难，换个说法试试？");

        log.info("DeepSeek API返回: length={}", content.length());
        return content.trim();
    }

    /**
     * 构建 ChatCompletion 请求参数。
     */
    private ChatCompletionCreateParams buildCompletionParams(String systemPrompt, String userPrompt) {
        return ChatCompletionCreateParams.builder()
                .model(model)
                .addMessage(buildSystemMessage(systemPrompt))
                .addMessage(buildUserMessage(userPrompt))
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();
    }

    /**
     * 构建 system 角色消息参数。
     */
    private ChatCompletionMessageParam buildSystemMessage(String systemPrompt) {
        ChatCompletionSystemMessageParam systemMessage = ChatCompletionSystemMessageParam.builder()
                .content(ChatCompletionSystemMessageParam.Content.ofTextContent(systemPrompt))
                .role(ChatCompletionSystemMessageParam.Role.SYSTEM)
                .build();
        return ChatCompletionMessageParam.ofChatCompletionSystemMessageParam(systemMessage);
    }

    /**
     * 构建 user 角色消息参数。
     */
    private ChatCompletionMessageParam buildUserMessage(String userPrompt) {
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(ChatCompletionUserMessageParam.Content.ofTextContent(userPrompt))
                .role(ChatCompletionUserMessageParam.Role.USER)
                .build();
        return ChatCompletionMessageParam.ofChatCompletionUserMessageParam(userMessage);
    }
}
