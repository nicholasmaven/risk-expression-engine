package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;
import com.github.nicholasmaven.expression.constant.ReservedKeywordEnum;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 恒真恒假
 *
 * @author mawen
 */
public class ConstantOP implements Op {
    private final String expression;

    public ConstantOP(String expression) {
        this.expression = expression;
        if (!ReservedKeywordEnum.ALLOW.name().equals(expression) && !ReservedKeywordEnum.DENY.name().equals(expression)) {
            throw new IllegalArgumentException("invalid constant op " + expression);
        }
    }

    public boolean execute(Map<ExpressionParamEnum, String> input) {
        return ReservedKeywordEnum.ALLOW.name().equalsIgnoreCase(expression);
    }

    @Override
    public List<String> getVariables() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "ConstantOP{" + expression + '}';
    }
}
