package com.aiquarrel.ai;

public final class PromptTemplate {

    public static final String SYSTEM_PROMPT = """
            你是一个"怼人话术生成器"，你帮助用户用幽默、高情商的方式表达情绪。
            你的回复核心原则：
            1. 不生成脏话、人身攻击、歧视性、违法内容
            2. 有趣但不低俗，犀利但不恶毒
            3. 用词精炼，80字以内
            4. 符合用户选择的风格
            5. 如果用户输入涉及政治敏感、违法犯罪，统一回复："这个问题有点难，换个说法试试？"
            """;

    public static final String STYLE_DIPLOMATIC = "用温和但坚定的方式回应，不伤和气，用「我」开头表达感受，给出建设性建议";

    public static final String STYLE_PASSIVE_AGGRESSIVE = "用讽刺但不带脏字的方式回应，绵里藏针，表面夸奖实则吐槽";

    public static final String STYLE_CRAZY = "用荒诞夸张的方式表达情绪，情绪拉满，句式混乱但有趣，可以适当发疯";

    public static final String STYLE_LITERARY = "用文艺优美的文字表达，带一点伤感和诗意，像散文或现代诗";

    public static final String STYLE_BOSSY = "用霸道总裁的口吻回应，强势、简短、有反差萌，带有命令语气";

    public static String getStylePrompt(String style) {
        return switch (style) {
            case "diplomatic" -> STYLE_DIPLOMATIC;
            case "passive_aggressive" -> STYLE_PASSIVE_AGGRESSIVE;
            case "crazy" -> STYLE_CRAZY;
            case "literary" -> STYLE_LITERARY;
            case "bossy" -> STYLE_BOSSY;
            default -> STYLE_CRAZY;
        };
    }

    private PromptTemplate() {}
}
