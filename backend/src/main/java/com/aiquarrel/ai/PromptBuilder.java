package com.aiquarrel.ai;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildSystemPrompt(String style) {
        String stylePrompt = PromptTemplate.getStylePrompt(style);
        return PromptTemplate.SYSTEM_PROMPT + "\n\n" + stylePrompt;
    }

    public String buildUserPrompt(String scene) {
        return "场景：" + scene + "\n请直接输出话术：";
    }
}
