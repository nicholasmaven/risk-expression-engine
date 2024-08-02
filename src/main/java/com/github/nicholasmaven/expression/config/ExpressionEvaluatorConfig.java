package com.github.nicholasmaven.expression.config;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Data
public class ExpressionEvaluatorConfig {
    private boolean skip = false;

    private Set<String> matchProductCodes;

    private Set<String> evaluateMatchProductCodes;

    private List<EvaluatingRule> settings;

    private List<String> purposes;

    public boolean applicableFor(String code) {
        for (String p : purposes) {
            if (p.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public boolean shouldSkipRule(String productCode) {
        return skip || (!CollectionUtils.isEmpty(matchProductCodes) && !matchProductCodes.contains(productCode));
    }

    public boolean shouldSkipEvaluator(String productCode) {
        return skip || (!CollectionUtils.isEmpty(evaluateMatchProductCodes) && !evaluateMatchProductCodes.contains(productCode));
    }

}
