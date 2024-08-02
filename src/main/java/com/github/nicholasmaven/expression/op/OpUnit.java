package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.EvaluationOperatorEnum;
import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;
import com.github.nicholasmaven.expression.constant.ReservedKeywordEnum;
import com.github.nicholasmaven.expression.exception.RuleConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author mawen
 */
@Slf4j
public class OpUnit implements Op {

    private final String expression;

    private String field;

    private EvaluationOperatorEnum operator;

    private String value;

    public OpUnit(String expression) throws RuleConfigException {
        this.expression = expression;
        parse();
    }

    private void parse() throws RuleConfigException {
        List<String> words = processExpression();
        if (words.size() < 3) {
            throw new RuleConfigException("invalid expression " + expression);
        }
        field = words.get(0);
        checkVariableName();

        if (words.size() > 1) {
            operator = EvaluationOperatorEnum.of(words.get(1));
            if (operator == null) {
                throw new RuleConfigException("unsupported operator " + words.get(1));
            }

            value = words.get(2);
            checkValue(operator);
        }
    }

    private void checkValue(EvaluationOperatorEnum opEnum) throws RuleConfigException {
        if (opEnum.isNumericOnly() && !NumberUtils.isNumber(value)) {
            throw new RuleConfigException("invalid value " + value + " for operator " + operator);
        }
        if (StringUtils.isEmpty(value) || invalidNullStr(value)) {
            throw new RuleConfigException("invalid empty/null value " + value);
        }
    }

    private void checkVariableName() throws RuleConfigException {
        if (StringUtils.isEmpty(field)) {
            throw new RuleConfigException("empty variable name");
        }
        if (ReservedKeywordEnum.of(field) != null) {
            throw new RuleConfigException("invalid variable name " + field + ", because it's a reserved keyword, consider another name");
        }
        if (!Character.isUpperCase(field.charAt(0)) && !Character.isLowerCase(field.charAt(0))) {
            throw new RuleConfigException("invalid variable name " + field + ", because it starts with non-alphabet");
        }
        if (field.indexOf('_') > -1) {
            throw new RuleConfigException("invalid variable name " + field + ", use '-' instead of '_'");
        }
    }

    private boolean invalidNullStr(String value) {
        return value.equalsIgnoreCase("null") && !value.equals("NULL") && !value.equals("null");
    }

    private List<String> processExpression() {
        List<String> returnWords = new ArrayList<>();
        String[] words = expression.split(" ");
        for (String word : words) {
            if (!"".equals(word.trim())) {
                returnWords.add(word.trim());
            }
        }
        return returnWords;
    }

    /**
     * 左侧为null值时, 只有equal/notEquals时才会做null比较, 其余操作符返回false
     */
    @Override
    public boolean execute(Map<ExpressionParamEnum, String> input) {
        ExpressionParamEnum param = ExpressionParamEnum.of(field);
        Assert.notNull(param, "field invalid or unsupported");
        return operator.eval(param, input, value);
    }

    public String toString() {
        String returnString = field;
        if (operator != null) {
            returnString += " " + operator;
        }
        if (value != null) {
            returnString += " " + value;
        }
        return returnString;
    }

    public List<String> getVariables() {
        return Collections.singletonList(field);
    }

    public String getExpression() {
        return expression;
    }
}
