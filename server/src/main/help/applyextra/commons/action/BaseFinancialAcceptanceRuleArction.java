package applyextra.commons.action;

import applyextra.businessrules.Rule;
import applyextra.businessrules.RuleHelper;

/**
 * Created by CL94WQ on 10-10-2017.
 */
public abstract class BaseFinancialAcceptanceRuleArction implements Rule {

    @Override
    public String getRuleId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRuleCheckingProcess() {
        return Rule.Type.FINANCIAL_ACCEPTANCE.name();
    }

    @Override
    public boolean canBeOverruled() {
        return RuleHelper.canBeOverruled(this);
    }
}
