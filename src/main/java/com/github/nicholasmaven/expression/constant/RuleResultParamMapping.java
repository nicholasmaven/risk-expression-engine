package com.github.nicholasmaven.expression.constant;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * xxx-rule-result 与 UnderwritingRuleProcessorTypeEnum 之间的映射转换
 *
 * @author mawen
 */
public abstract class RuleResultParamMapping {

    /**
     * <pre>
     * 大写下划线转小写中划线
     * example: XX1_RULE -> xx1-rule-result
     */
    public static ExpressionParamEnum ruleToResultParam(UnderwritingRuleProcessorTypeEnum rule) {
        Assert.notNull(rule, "UnderwritingRuleTypeEnum is null");
        String name = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, rule.name()) + "-result";
        return ExpressionParamEnum.of(name);
    }

    /**
     * <pre>
     * 小写中划线转大写下划线转
     * example: xx1-rule-result -> XX1_RULE
     */
    public static UnderwritingRuleProcessorTypeEnum resultParamToRule(ExpressionParamEnum param) {
        Assert.notNull(param, "ExpressionParamEnum is null");
        String name = param.getKey();

        //name必须为xxx-rule-result格式
        if (name.length() <= 12 || !name.endsWith("-rule-result")) {
            throw new IllegalArgumentException("invalid param name " + name);
        }
        name = StringUtils.removeEnd(name, "-result");
        name = CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, name);
        return UnderwritingRuleProcessorTypeEnum.of(name);
    }
}
