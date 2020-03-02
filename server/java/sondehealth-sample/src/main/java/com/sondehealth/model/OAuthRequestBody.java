package com.sondehealth.model;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthRequestBody {

	private String grant_type;
	private String scope;

	public String getGrant_type() {
		return grant_type;
	}

	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		return "grant_type=" + grant_type + "&scope=" + scope;
	}

}
