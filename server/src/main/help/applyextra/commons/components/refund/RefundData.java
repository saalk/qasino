package applyextra.commons.components.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import applyextra.commons.model.refund.RefundItem;

import java.io.IOException;
import java.io.Serializable;

@Slf4j
@NoArgsConstructor
public class RefundData implements Serializable {

	private String jsonData = null;

	public static String convertRefundItemToJson(RefundItem refundItem) {
		final ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(refundItem);
		} catch (IOException e) {
			log.error("Could not convert {} to json", refundItem);
		}
		return json;

	}

	public static <E extends RefundItem> E convertJsonToRefundItem(String jsonData, Class<E> clazz) {
		final ObjectMapper mapper = new ObjectMapper();
		E result = null;
		try {
			result = mapper.readValue(jsonData, clazz);
		} catch (IOException e) {
			log.error("Could not convert {} to {}", jsonData, clazz.getName());
		}
		return result;

	}

	public static <E extends RefundItem> RefundData from(E refundItem) {
		final RefundData result = new RefundData();
		result.setJson(convertRefundItemToJson(refundItem));
		return result;
	}

	public <E extends RefundItem> RefundData(E refundItem) {
		this.jsonData = convertRefundItemToJson(refundItem);
	}

	public <E extends RefundItem> E as(Class<E> clazz) {
		return convertJsonToRefundItem(this.jsonData, clazz);
	}

	public String getJson() {
		return jsonData;
	}

	public RefundData setJson(String data) {
		jsonData = data;
		return this;
	}

}
