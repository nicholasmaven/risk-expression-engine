package com.github.nicholasmaven.expression;

import com.github.nicholasmaven.expression.constant.EvaluationResultEnum;
import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;
import com.github.nicholasmaven.expression.constant.ReservedKeywordEnum;
import com.github.nicholasmaven.expression.exception.RuleConfigException;
import com.github.nicholasmaven.expression.op.AbstractOpGroup;
import com.github.nicholasmaven.expression.op.AndOp;
import com.github.nicholasmaven.expression.op.ConstantOP;
import com.github.nicholasmaven.expression.op.NotOp;
import com.github.nicholasmaven.expression.op.Op;
import com.github.nicholasmaven.expression.op.OpUnit;
import com.github.nicholasmaven.expression.op.OrOp;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static com.github.nicholasmaven.expression.constant.EvaluationResultEnum.PASS;
import static com.github.nicholasmaven.expression.constant.EvaluationResultEnum.REJECT;

@Slf4j
@Builder
@Getter
public class Expression {

    private String conditionExpression;

    private String decisionExpression;

    private Op conditionRuleTree;

    private Op decisionRuleTree;

    private final EvaluationResultEnum result;

    private String msg;

    /**
     * 返回不为null
     */
    public List<String> getVariables() {
        LinkedHashSet<String> vars = new LinkedHashSet<>();
        vars.addAll(conditionRuleTree.getVariables());
        vars.addAll(decisionRuleTree.getVariables());
        return new ArrayList<>(vars);
    }

    public EvaluationResultEnum execute(Map<ExpressionParamEnum, String> input) {
        if (!conditionRuleTree.execute(input)) {
            return EvaluationResultEnum.SKIP;
        }
        return decisionRuleTree.execute(input) ? result : result == PASS ? REJECT : PASS;
    }

    // 解析表达式
    public void parse() throws RuleConfigException {
        List<Object> conditionRules = parse(conditionExpression);
        if (conditionRules.isEmpty()) {
            throw new RuleConfigException("missing conditionRule");
        }
        conditionRuleTree = (Op) conditionRules.get(0);

        List<Object> decisionRules = parse(decisionExpression);
        if (decisionRules.isEmpty()) {
            throw new RuleConfigException("missing decisionRule");
        }
        decisionRuleTree = (Op) decisionRules.get(0);
    }

    private List<Object> parse(String expression) throws RuleConfigException {
        List<Object> splitWords = splitExpression(expression);
        parseRuleUnit(splitWords);
        int parseCount = 0;
        try {
            while (getIndex(splitWords, ReservedKeywordEnum.AND.name()) != null || getIndex(splitWords, ReservedKeywordEnum.OR.name()) != null) {
                Integer andIndex = getOperIndex(splitWords, ReservedKeywordEnum.AND.name());
                if (andIndex != null) {
                    andIndex = processOpGroupRuleTree(splitWords, andIndex, AndOp.class);
                    processBracket(splitWords, andIndex);
                    continue;
                }
                Integer orIndex = getOperIndex(splitWords, ReservedKeywordEnum.OR.name());
                if (orIndex != null) {
                    orIndex = processOpGroupRuleTree(splitWords, orIndex, OrOp.class);
                    processBracket(splitWords, orIndex);
                }
                if (parseCount++ > 1000) {
                    throw new IllegalArgumentException("parse expression error: expression=" + expression);
                }
            }
            return splitWords;
        } catch(ReflectiveOperationException e) {
            throw new RuleConfigException("expression " + expression + " reflective newInstance GroupOp error");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends AbstractOpGroup> Integer processOpGroupRuleTree(List<Object> splitWords, Integer index, Class<T> clazz)
            throws ReflectiveOperationException {
        Op prev = (Op) splitWords.get(index - 1);
        Op next = (Op) splitWords.get(index + 1);
        T tmp = null;
        List<Op> children = new ArrayList<>();
        if (clazz.isAssignableFrom(prev.getClass())) {
            tmp = (T) prev;
            children.addAll(tmp.getChildren());
            children.add(next);
        } else if (clazz.isAssignableFrom(next.getClass())) {
            tmp = (T) next;
            children.add(prev);
            children.addAll(tmp.getChildren());
        } else {
            children.add(prev);
            children.add(next);
        }

        Constructor<T> ctor = clazz.getConstructor(String.class, List.class);
        T dst = ctor.newInstance(tmp == null ? null : tmp.getExpression(), children);
        splitWords.set(index, dst);
        splitWords.remove(index + 1);
        splitWords.remove(index - 1);
        return index - 1;
    }

    // 处理括号和非语句
    private void processBracket(List<Object> splitWords, Integer ruleIndex) {
        if (ruleIndex == 0 || ruleIndex == splitWords.size() - 1) {
            return;
        }
        Object nextWord = splitWords.get(ruleIndex + 1);
        Object prevWord = splitWords.get(ruleIndex - 1);
        while ("(".equals(prevWord) && ")".equals(nextWord)) {
            splitWords.remove(ruleIndex + 1);
            splitWords.remove(ruleIndex - 1);
            ruleIndex -= 1;
            ruleIndex = processNot(splitWords, ruleIndex);
            if (ruleIndex == 0 || ruleIndex == splitWords.size() - 1) {
                return;
            }
            nextWord = splitWords.get(ruleIndex + 1);
            prevWord = splitWords.get(ruleIndex - 1);
        }
    }

    private Integer processNot(List<Object> splitWords, Integer ruleIndex) {
        if (ruleIndex == 0) {
            return ruleIndex;
        }
        Object prevWord = splitWords.get(ruleIndex - 1);
        // 处理非语句
        while (prevWord.equals("!")) {
            if (!(splitWords.get(ruleIndex) instanceof Op)) {
                return ruleIndex;
            }
            Op ruleTree = (Op) splitWords.get(ruleIndex);
            NotOp notOp = new NotOp(ruleTree);
            splitWords.set(ruleIndex, notOp);
            splitWords.remove(ruleIndex - 1);
            ruleIndex -= 1;
            prevWord = splitWords.get(ruleIndex - 1);
        }
        return ruleIndex;
    }

    private Integer getIndex(List<Object> splitWords, String word) {
        for (int i = 0; i < splitWords.size(); i++) {
            Object object = splitWords.get(i);
            if (object instanceof String && word.equals(object)) {
                return i;
            }
        }
        return null;
    }

    private Integer getOperIndex(List<Object> splitWords, String word) {
        for (int i = 0; i < splitWords.size(); i++) {
            Object object = splitWords.get(i);
            if (!(object instanceof String)) {
                continue;
            }
            if (!word.equals(object)) {
                continue;
            }
            if (i == 0) {// 第一个元素
                continue;
            }
            if (!(splitWords.get(i - 1) instanceof Op)) {
                continue;
            }
            if (i == splitWords.size() - 1) {// 最后一个元素
                continue;
            }
            if (!(splitWords.get(i + 1) instanceof Op)) {
                continue;
            }
            return i;
        }
        return null;
    }

    private void parseRuleUnit(List<Object> splitWords) throws RuleConfigException {
        for (int i = 0; i < splitWords.size(); i++) {
            Object object = splitWords.get(i);
            String str = (String) object;
            if ("ALLOW".equals(str) || "DENY".equals(str)) {
                ConstantOP cop = new ConstantOP(str);
                splitWords.set(i, cop);
            } else if (str.contains(" ")) {
                OpUnit opUnit = new OpUnit(str);
                splitWords.set(i, opUnit);
            }
        }
        // 处理非语句
        for (int i = 0; i < splitWords.size(); i++) {
            processNot(splitWords, i);
        }
    }

    private List splitExpression(String expression) {
        List<String> returnWord = new ArrayList();
        returnWord.add(expression);
        returnWord = split(returnWord, " AND ");
        returnWord = split(returnWord, " OR ");
        returnWord = split(returnWord, "(");
        returnWord = split(returnWord, ")");
        return returnWord;
    }

    private List<String> split(List<String> words, String splitWord) {
        List<String> returnWord = new ArrayList<>();
        for (String wordTmp : words) {
            wordTmp = wordTmp.trim();
            while (wordTmp.contains(splitWord)) {
                int index = wordTmp.indexOf(splitWord);
                if (index == 0) {// 开头位置
                    returnWord.add(splitWord);
                } else {
                    returnWord.add(wordTmp.substring(0, index).trim());
                    returnWord.add(splitWord);
                }
                // 处理剩下一段
                wordTmp = wordTmp.substring(index + splitWord.length());
                wordTmp = wordTmp.trim();
            }
            if (!wordTmp.isEmpty()) {
                returnWord.add(wordTmp);
            }
        }
        return returnWord;
    }

}
