package com.github.nicholasmaven.expression.constant;

import java.math.BigDecimal;

/**
 * 表达式关键字
 */
public enum ExpressionParamEnum {
    PRODUCT("product", String.class),

    TENOR("tenor", Integer.class),

    XX1_RULE_RESULT("xx1-rule-result", String.class),

    SCORE_RULE_RESULT("score-rule-result", String.class),

    A_SCORE("a-score", BigDecimal.class),

    XX_SCORE("xx-score", BigDecimal.class),

    BUREAU_SCORE("bureau-score", BigDecimal.class),

    MAX_DPD_CODE("max-dpd-code", String.class),

    XX_FLAG("xx-flag", String.class),

    ;

    /**
     * key不能重复
     */
    private final String key;

    private final Class<?> type;

    ExpressionParamEnum(String key, Class<?> type) {
        this.key = key;
        this.type = type;
    }

    public static ExpressionParamEnum of(String key) {
        return EnumUtils.lookupByProperty(ExpressionParamEnum.class, "key", ExpressionParamEnum::getKey, key);
    }

    public String getKey() {
        return key;
    }

    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return name() + '{' +
                "key=" + key +
                ", type=" + type.getSimpleName() +
                '}';
    }
}
