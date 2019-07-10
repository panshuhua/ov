package com.ivay.ivay_common.dto;

public enum SMSResponseStatus {
	SUCCESS("0", "success"),
	MISSPARAMS("2","Missing params"),
	InvalidCredentials("4","Invalid credentials"),
	UnauthorizedIP("5","Unauthorized IP"),
	InvalidPhoneNumber("6","Invalid phone number"),
	InvalidSenderId("7","Invalid sender id"),
	MessageBombingDetected("8","Message bombing detected"),
	QuotaExceeded("9","Quota exceeded"),
	ThrottlingError("10","Throttling error"),
	SystemEror("11","System error");
	
	private String code;
	private String message;
	
	SMSResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
