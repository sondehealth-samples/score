package com.sondehealth.services;

import javax.security.auth.message.AuthException;

import com.sondehealth.model.OAuthRequestBody;
import com.sondehealth.model.OAuthResponse;

public interface AuthTokenService {

	public OAuthResponse getOauthToken(String basicAuth, OAuthRequestBody oAuthRequestBody) throws AuthException;
}
