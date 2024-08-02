package com.github.nicholasmaven.expression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.nicholasmaven.expression.constant.EvaluationResultEnum;
import com.github.nicholasmaven.expression.constant.ExpressionParamEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mawen
 */
public class ExpressionEvalOutput {

    private static final ExpressionEvalOutput _SKIP = ExpressionEvalOutput.builder().build();

    @Getter
    private EvaluationResultEnum result;

    @Getter
    private String rejectReason;

    @Getter
    private String input;

    @Getter
    @Setter
    private String ruleOutputParams;

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toInputString(Map<ExpressionParamEnum, String> input) {
        if (MapUtils.isEmpty(input)) {
            return "{}";
        }
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<ExpressionParamEnum, String> e : input.entrySet()) {
            String key = e.getKey().getKey();
            String value = e.getValue();
            map.put(key, value);
        }

         try {
            return mapper.writeValueAsString(map);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ExpressionEvalOutput skip() {
        return _SKIP;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "EvalOutput{"
                + "result=" + result
                + ",rejectReason=" + rejectReason
                + ",input=" + input
                + '}';
    }

    public static class Builder {

        private EvaluationResultEnum _result;

        private String _rejectReason;

        private Map<ExpressionParamEnum, String> _inputParams;

        private String _input;

        private String _ruleOutputParams;

        public Builder result(EvaluationResultEnum result) {
            this._result = result;
            return this;
        }

        public Builder rejectReason(String rejectReason) {
            this._rejectReason = rejectReason;
            return this;
        }

        public Builder inputParams(Map<ExpressionParamEnum, String> inputParams) {
            Assert.notNull(inputParams, "inputParams is null");
            this._inputParams = inputParams;
            return this;
        }

        public Builder input(String input) {
            this._input = input;
            return this;
        }

        public ExpressionEvalOutput build() {
            ExpressionEvalOutput output = new ExpressionEvalOutput();
            output.result = _result;
            output.input = _input == null ? toInputString(_inputParams) : _input;
            output.rejectReason = _rejectReason;
            return output;
        }
    }

}
