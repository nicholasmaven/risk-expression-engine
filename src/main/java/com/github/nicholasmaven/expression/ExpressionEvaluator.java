package com.github.nicholasmaven.expression;

import com.github.nicholasmaven.expression.config.EvaluatingRule;
import com.github.nicholasmaven.expression.config.ExpressionEvaluatorConfig;
import com.github.nicholasmaven.expression.constant.EvaluationResultEnum;
import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;
import com.github.nicholasmaven.expression.constant.RuleResultParamMapping;
import com.github.nicholasmaven.expression.exception.RuleConfigException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.nicholasmaven.expression.constant.EvaluationResultEnum.PASS;
import static com.github.nicholasmaven.expression.constant.EvaluationResultEnum.REJECT;
import static com.github.nicholasmaven.expression.constant.EvaluationResultEnum.SKIP;

/**
 * 解析与执行表达式
 * @author mawen
 */
@Slf4j
@Component
public class ExpressionEvaluator {

    public static final String RESULT_PARAM_SUFFIX = "-rule-result";
    private static final Predicate<String> PREDICATE = ((Predicate<String>) str -> str.endsWith(RESULT_PARAM_SUFFIX))
            .and(str -> Objects.isNull(RuleResultParamMapping.resultParamToRule(ExpressionParamEnum.of(str))));

    private final Map<String, Expression> exprCache = new ConcurrentHashMap<>();

    /**
     * 解析表达式并且检查变量合法性
     */
    public void load(List<EvaluatingRule> rules) throws RuleConfigException {
        Assert.notEmpty(rules, "Evaluating rules are empty");
        LOGGER.info("loading rule configuration...format: [conditionRule]:[decisionRule]:[result]:[msg]");

        List<String> undefinedParams = new ArrayList<>();
        List<String> invalidResultParams = new ArrayList<>();
        for (EvaluatingRule rule : rules) {
            String key = rule.toString();
            Expression expr = Expression.builder()
                    .conditionExpression(rule.getConditionRule())
                    .decisionExpression(rule.getDecisionRule())
                    .result(EvaluationResultEnum.of(rule.getResult()))
                    .msg(rule.getMsg())
                    .build();

            LOGGER.info("parse new expr:rule={}", rule);
            expr.parse();

            undefinedParams.addAll(checkUndefinedParams(expr));
            invalidResultParams.addAll(checkInvalidResultParams(expr));

            exprCache.put(key, expr);
        }
        if (!undefinedParams.isEmpty()) {
            String hint = "missing variable definition, check ExpressionParamEnum: [" +
                    String.join(",", undefinedParams) +
                    "];";
            throw new RuleConfigException(hint);
        }

        if (!invalidResultParams.isEmpty()) {
            String hint = "invalid rule type, check UnderwritingRuleTypeEnum: [" +
                    String.join(",", invalidResultParams) +
                    "];";
            throw new RuleConfigException(hint);
        }
        Assert.notEmpty(exprCache, "invalid evaluating rule");
    }

    private List<String> checkUndefinedParams(Expression expr) {
        List<String> vars = expr.getVariables();
        return vars.isEmpty() ? Collections.emptyList() : vars.stream()
                .filter(e -> Objects.isNull(ExpressionParamEnum.of(e)))
                .collect(Collectors.toList());
    }

    private List<String> checkInvalidResultParams(Expression expr) {
        List<String> vars = expr.getVariables();
        return vars.isEmpty() ? Collections.emptyList() : vars.stream()
                .filter(PREDICATE)
                .collect(Collectors.toList());
    }

    public ExpressionEvalOutput execute(Map<ExpressionParamEnum, String> input, ExpressionEvaluatorConfig config) {
        for (EvaluatingRule rule : config.getSettings()) {
            Expression expr = exprCache.get(rule.toString());

            EvaluationResultEnum expressionEvaluationResultEnum = expr.execute(input);
            if (expressionEvaluationResultEnum == SKIP) {
                continue;
            }
            if (expressionEvaluationResultEnum == PASS) {
                return ExpressionEvalOutput.builder()
                        .result(PASS)
                        .inputParams(consumedRuleParams(input, config.getSettings()))
                        .build();
            }
            if (expressionEvaluationResultEnum == REJECT) {
                return ExpressionEvalOutput.builder()
                        .result(REJECT)
                        .inputParams(consumedRuleParams(input, config.getSettings()))
                        .rejectReason(expr.getMsg())
                        .build();
            }
        }
        return ExpressionEvalOutput.builder()
                .result(SKIP)
                .inputParams(consumedRuleParams(input, config.getSettings()))
                .build();
    }

    private Map<ExpressionParamEnum, String> consumedRuleParams(Map<ExpressionParamEnum, String> input,
                                                                List<EvaluatingRule> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyMap();
        }
        List<ExpressionParamEnum> params = rules.stream()
                .map(e -> exprCache.get(e.toString()))
                .map(Expression::getVariables)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .distinct()
                .map(ExpressionParamEnum::of)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<ExpressionParamEnum, String> map = new HashMap<>();
        for (ExpressionParamEnum param : params) {
            map.put(param, input.get(param));
        }

        return map;
    }

}
