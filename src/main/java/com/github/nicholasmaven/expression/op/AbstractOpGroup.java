package com.github.nicholasmaven.expression.op;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mawen
 */
public abstract class AbstractOpGroup implements Op {

    private final String expression;

    private final List<Op> children;

    public AbstractOpGroup(String expression, List<Op> children) {
        this.expression = expression;
        this.children = new ArrayList<>(children);
    }

    public String getExpression() {
        return expression;
    }

    public List<Op> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<String> getVariables() {
        return getChildren().stream()
                .map(Op::getVariables)
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
