package applyextra.commons.util;

import applyextra.businessrules.RejectedRuleDTO;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RejectedRulesUtil {
    public static Set<RejectedRuleDTO> addToSet(List<RejectedRuleDTO> rejectedRules) {
        Set<RejectedRuleDTO> rejectedRuleSet = new LinkedHashSet<>();
        if (rejectedRules.size() > 0) {
            rejectedRuleSet.addAll(rejectedRules);
        }
        return rejectedRuleSet;
    }

    public static Boolean isPassed(Set<RejectedRuleDTO> rejectedRuleSet) {
        return rejectedRuleSet.stream()
                .allMatch(RejectedRuleDTO::isCanBeOverruled);
    }
}
