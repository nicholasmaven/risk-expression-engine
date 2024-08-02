package com.github.nicholasmaven.expression.op;

import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;

import java.util.List;
import java.util.Map;

/**
 * 表达式算子, 算子之间可以组合成tree
 * @author mawen
 */
public interface Op {

    boolean execute(Map<ExpressionParamEnum, String> input);

    /**
     * 不能返回null
     */
    List<String> getVariables();

}
