package com.github.nicholasmaven.expression.config;

import com.github.nicholasmaven.expression.constant.EvaluationResultEnum;
import com.github.nicholasmaven.expression.exception.RuleConfigException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * <pre>
 * 变量名规范:
 * 1. 变量需要在ExpressionParamEnum中定义
 * 2. 规则结果的变量名小写用'-'拼接: xx-rule-result
 */
@Getter
public class EvaluatingRule {
    // age >= 19 AND age <= 20 AND ( SLIK-KOL = 1 OR SLIK-KOL = null )
    private String conditionRule;
    // (( xx1-score < XXX AND a-score < XXX ) OR ( xx1-score < XXX AND a-score < XXX ))
    private String decisionRule;
    // pass / reject
    private String result;
    // SCORE1
    private String msg;

    public String toString() {
        return conditionRule + ":" + decisionRule + ":" + result + ":" + msg;
    }

    public void setConditionRule(String conditionRule) throws RuleConfigException {
        if (StringUtils.isEmpty(conditionRule)) {
            throw new RuleConfigException("condition rule is empty");
        }
        this.conditionRule = conditionRule;
    }

    public void setDecisionRule(String decisionRule) throws RuleConfigException {
        if (StringUtils.isEmpty(decisionRule)) {
            throw new RuleConfigException("decision rule is empty");
        }
        this.decisionRule = decisionRule;
    }

    public void setResult(String result) throws RuleConfigException {
        if (EvaluationResultEnum.of(result) == null || EvaluationResultEnum.SKIP.name().equals(result)) {
            throw new RuleConfigException("result is invalid");
        }
        this.result = result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
