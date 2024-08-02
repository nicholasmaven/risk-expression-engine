package com.github.nicholasmaven.expression.constant;

import lombok.Getter;

/**
 * 授信评估执行结果
 */
@Getter
public enum EvaluationResultEnum {

    /**
     * 执行通过
     */
    PASS,

    /**
     * 拒绝
     */
    REJECT,

    /**
     * 跳过
     */
    SKIP,
    ;

    public static EvaluationResultEnum of(String name) {
        return EnumUtils.lookupByEnumName(EvaluationResultEnum.class, name);
    }

}
