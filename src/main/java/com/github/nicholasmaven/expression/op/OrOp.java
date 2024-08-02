package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;

import java.util.List;
import java.util.Map;

/**
 * @author mawen
 */
public class OrOp extends AbstractOpGroup {

    public OrOp(String expression, List<Op> children) {
        super(expression, children);
    }

    @Override
    public boolean execute(Map<ExpressionParamEnum, String> input) {
        for (Op child : getChildren()) {
            if (child.execute(input)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String str = "";
        for (Op ruleTree : getChildren()) {
            str += " OR ";
            str += ruleTree.toString();
        }
        if (str.length() > 0) {
            str = str.substring(4);
        }
        return "(" + str + ")";
    }
}
