package com.sondehealth.apiserver.model;

public class OAuthResponse {

	private Object response;
	private Integer StatusCode;
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
	public Integer getStatusCode() {
		return StatusCode;
	}
	public void setStatusCode(Integer statusCode) {
		StatusCode = statusCode;
	}
	
}
