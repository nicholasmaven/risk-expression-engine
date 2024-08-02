package com.github.nicholasmaven.expression.constant;

/**
 * 授信阶段枚举，长度最长16
 * 同时也是rule type枚举
 */
public enum UnderwritingRuleProcessorTypeEnum {
    XX1_RULE("XX1 Rule"),
    /**
     * Model A score
     */
    SCORE_RULE("Score Rule"),
    /**
     * final规则
     */
    FINAL_RULE("Final Rule"),

    WHITELIST("Live-Testing Whitelist Query"),

    STRATEGY_LAYER1("Strategy Layer 1"),

    STRATEGY_LAYER2("Strategy Layer 2"),

    STRATEGY_LAYER3("Strategy Layer 3"),

    ;

    private final String display;

    UnderwritingRuleProcessorTypeEnum(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public static UnderwritingRuleProcessorTypeEnum of(String name) {
        return EnumUtils.lookupByEnumName(UnderwritingRuleProcessorTypeEnum.class, name);
    }
}
