package applyextra.commons.model.refund;


import java.util.List;

public interface Refund {

    void addRefundItem(RefundItem item);

    List<RefundItem> getRefundItems();

}
