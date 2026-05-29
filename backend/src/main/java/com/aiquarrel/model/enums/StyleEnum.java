package com.aiquarrel.model.enums;

import lombok.Getter;

@Getter
public enum StyleEnum {
    DIPLOMATIC("diplomatic", "高情商版"),
    PASSIVE_AGGRESSIVE("passive_aggressive", "阴阳怪气版"),
    CRAZY("crazy", "发疯文学版"),
    LITERARY("literary", "文艺版"),
    BOSSY("bossy", "霸总版");

    private final String code;
    private final String name;

    StyleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static StyleEnum fromCode(String code) {
        for (StyleEnum style : values()) {
            if (style.code.equals(code)) {
                return style;
            }
        }
        return null;
    }

    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
