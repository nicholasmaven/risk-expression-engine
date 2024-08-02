### 组件目标
满足信贷风控业务和产品的策略配置需求, 具体来说
1. 容易理解, 尽可能减小学习成本
2. 支持条件过滤和决策结果, 即满足一定条件下才有决策结果, 条件不满足时跳过

来自业务方的需求配置如下:

XX1-RULE
```
If max dpd code is in {Max DPD code list 3,4,5,6}
Then {Rejection Code}
Else "Pass"
```

SCORE-RULE

![风控规则](风控规则示例(已脱敏).png)

FINAL-RULE

![风控最终规则](风控最终决策规则示例(已脱敏).png)

### 组件使用
#### XX1-RULE配置
```
xx1.rule1.conditionRule = max-dpd-code != null
xx1.rule1.descisionRule = max-dpd-code = 3 OR max-dpd-code = 4 OR max-dpd-code = 5 OR max-dpd-code = 6
xx1.rule1.result = REJECT
xx1.rule1.msg = XX1_DPD_{3,4,5,6}
```

#### SCORE-RULE配置
```
##第一条规则
score.rule1.conditionRule = product = P01 AND tenor = 1 AND ntb-flag = 1
score.rule1.decisionRule = (xx1-score <= 600 AND a-score <= 560) OR (xx1-score <= 550 AND a-score <= 620) OR (xx1-score < 450 AND a-score = null)
score.rule1.result = REJECT
score.rule1.msg = XXLOAN_SCORE_1

##第二条规则
score.rule2.conditionRule = product = P01 AND (tenor = 1 OR tenor = 3 OR tenor=6 OR tenor = 12) AND ntb-flag = 0 AND a-score = null
score.rule2.decisionRule = ALLOW
score.rule2.result = REJECT
score.rule2.msg = XXLOAN_SCORE_2

##第三条规则
score.rule3.conditionRule = product = P01 AND (tenor = 1 OR tenor = 3 OR tenor=6 OR tenor = 12) AND ntb-flag = null
score.rule3.decisionRule = xx1-score > 0
score.rule3.result = REJECT
score.rule3.msg = XXLOAN_SCORE_3
```

#### FINAL-RULE
```
final.rule1.conditionRule = crif-rule-result = PASS AND score-rule-result = REJECT
final.rule1.decisionRule = ALLOW
final.rule1.result = REJECT

final.rule2.conditionRule = crif-rule-result = REJCT AND score-rule-result = PASS
final.rule2.decisionRule = ALLOW
final.rule2.result = REJECT
```
### 组件设计
总的来说, 规则语句分为2类
1. 条件规则: 起到过滤作用, 满足条件的才会执行决策规则
2. 决策规则: 规则满足则放行