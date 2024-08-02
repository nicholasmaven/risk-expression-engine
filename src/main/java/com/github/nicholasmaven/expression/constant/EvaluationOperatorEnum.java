package com.github.nicholasmaven.expression.constant;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author mawen
 */
public enum EvaluationOperatorEnum {
    LT("<") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (StringUtils.isEmpty(var)) {
                return false;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) < Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) < 0;
            }
            return false;
        }
    },
    LTE("<=") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (StringUtils.isEmpty(var)) {
                return false;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) <= Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) <= 0;
            }
            return false;
        }
    },
    GT(">") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (StringUtils.isEmpty(var)) {
                return false;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) > Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) > 0;
            }
            return false;
        }
    },
    GTE(">=") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (StringUtils.isEmpty(var)) {
                return false;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) >= Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) >= 0;
            }
            return false;
        }
    },
    EQ("=") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (value.equals("null") || value.equals("NULL")) {
                return StringUtils.isEmpty(var);
            } else if (StringUtils.isEmpty(var)) {
                return false;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) == Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) == 0;
            } else {
                return var.equals(value);
            }
        }
    },
    NEQ("!=") {
        @Override
        public boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value) {
            String var = input.get(param);
            if (value.equals("null") || value.equals("NULL")) {
                return !StringUtils.isEmpty(var);
            } else if (StringUtils.isEmpty(var)) {
                return true;
            } else if (param.getType() == Integer.class) {
                return Integer.parseInt(var) != Integer.parseInt(value);
            } else if (param.getType() == BigDecimal.class) {
                return new BigDecimal(var).compareTo(new BigDecimal(value)) != 0;
            } else {
                return !var.equals(value);
            }
        }
    },

    ;

    private final String value;

    EvaluationOperatorEnum(String value) {
        this.value = value;
    }

    public abstract boolean eval(ExpressionParamEnum param, Map<ExpressionParamEnum, String> input, String value);

    public static EvaluationOperatorEnum of(String op) {
        return EnumUtils.lookupByProperty(EvaluationOperatorEnum.class, "value", EvaluationOperatorEnum::getValue, op);
    }

    public String getValue() {
        return value;
    }

    public boolean isNumericOnly() {
        return this == LT || this == GT || this == LTE || this == GTE;
    }

}
