package cloud.qasino.card.controller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ControllerResponse {
	
	private Reason reason;
	private String data;
	private Object payload;
	
	public enum Reason {
		SUCCESS, FAILURE
	}
	
	public static ControllerResponse success() {
		return new ControllerResponse(Reason.SUCCESS);
	}
	
	public static ControllerResponse failure() {
		return new ControllerResponse(Reason.FAILURE);
	}
	
	public static ControllerResponse success(final String data) {
		return new ControllerResponse(Reason.SUCCESS, data);
	}
	
	public static ControllerResponse failure(final String data) {
		return new ControllerResponse(Reason.FAILURE, data);
	}
	
	public ControllerResponse(final Reason reason) {
		super();
		this.reason = reason;
	}
	
	public ControllerResponse(final Reason reason, String data) {
		super();
		this.reason = reason;
		this.data = data;
	}
	
	public boolean isSuccess() {
		return Reason.SUCCESS.equals(reason);
	}
	
	public boolean isFailure() {
		return Reason.FAILURE.equals(reason);
	}
	
}
