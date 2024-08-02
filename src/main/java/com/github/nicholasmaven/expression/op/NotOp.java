package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;

import java.util.List;
import java.util.Map;

/**
 * @author mawen
 */
public class NotOp implements Op {

    private final String expression;

    private final Op children;

    public NotOp(String expression, Op children) {
        this.expression = expression;
        this.children = children;
    }

    public NotOp(Op children) {
        this.expression = null;
        this.children = children;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public boolean execute(Map<ExpressionParamEnum, String> input) {
        return !children.execute(input);
    }

    public List<String> getVariables() {
        return children.getVariables();
    }

    public String toString() {
        if (children instanceof OrOp) {
            return "!" + children;
        } else {
            return "!(" + children + ")";
        }
    }
}
