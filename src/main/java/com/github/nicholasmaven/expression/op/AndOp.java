package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;

import java.util.List;
import java.util.Map;

/**
 * @author mawen
 */
public class AndOp extends AbstractOpGroup {

    public AndOp(String expression, List<Op> children) {
        super(expression, children);
    }

    @Override
    public boolean execute(Map<ExpressionParamEnum, String> input) {
        for (Op ruleUnit : getChildren()) {
            if (!ruleUnit.execute(input)) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        String str = "";
        for (Op ruleTree : getChildren()) {
            str += " AND ";
            str += ruleTree.toString();
        }
        if (str.length() > 0) {
            str = str.substring(5);
        }
        return str;
    }
}
