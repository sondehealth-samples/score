package com.sondehealth.services.impl;

import javax.security.auth.message.AuthException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.sondehealth.model.Constants;
import com.sondehealth.model.OAuthRequestBody;
import com.sondehealth.model.OAuthResponse;
import com.sondehealth.rest.controller.FrontController;
import com.sondehealth.services.AuthTokenService;
import com.sondehealth.services.UtilityService;

@Service
public class AuthTokenServiceImpl implements AuthTokenService{
	
	@Autowired
	UtilityService utilityService;
	
	Logger logger = LoggerFactory.getLogger(FrontController.class);

	@Override
	public OAuthResponse getOauthToken(String basicAuth, OAuthRequestBody oAuthRequestBody) throws AuthException {
		OAuthResponse response = null;
		String responseBody = null;
		HttpRequestWithBody http = Unirest.post(Constants.OAuthApiURL);
		int statusCode = 200;
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    try {
	        HttpResponse<String> httpResponse = http
	                .header("Content-Type", "application/x-www-form-urlencoded")
	                .header("Authorization", basicAuth).body(oAuthRequestBody.toString())
	                .asString();
	        statusCode = httpResponse.getStatus();
	        responseBody = httpResponse.getBody();
	        logger.info("Response status code - "+ statusCode + ", Response body - " + responseBody);
	        response = new OAuthResponse();
	        response.setResponse(gson.fromJson(responseBody, Object.class));
	        response.setStatusCode(statusCode); 
	        
	    } catch (UnirestException e) {
	        e.printStackTrace();
	        throw new AuthException("Unirest error");
	    }
	    return response;
	}

}
