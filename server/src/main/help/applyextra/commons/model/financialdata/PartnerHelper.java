package applyextra.commons.model.financialdata;

import org.springframework.util.StringUtils;

public class PartnerHelper {

    public static boolean hasId(Partner p) {
        return p != null && StringUtils.hasText(p.getId());
    }

    public static boolean hasDetails(Partner p) {
        return p != null && (StringUtils.hasText(p.getInitials())
                || StringUtils.hasText(p.getLastName())
                || p.getBirthDate() != null);
    }
}
