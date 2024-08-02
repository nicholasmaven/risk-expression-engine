package com.github.nicholasmaven.expression.constant;

/**
 * @author mawen
 */
public enum ReservedKeywordEnum {
    ALLOW,

    DENY,

    AND,

    OR,

    PASS,

    REJECT,

    SKIP,

    NULL,

    ;

    public static ReservedKeywordEnum of(String name) {
        return EnumUtils.lookupByEnumName(ReservedKeywordEnum.class, name);
    }
}
