package com.aiquarrel.ai;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildUserPrompt(String scene, String style) {
        String stylePrompt = PromptTemplate.getStylePrompt(style);
        return String.format("场景：%s\n风格要求：%s\n请根据以上场景和风格，生成一段怼人话术：", scene, stylePrompt);
    }

    public String getSystemPrompt() {
        return PromptTemplate.SYSTEM_PROMPT;
    }
}
