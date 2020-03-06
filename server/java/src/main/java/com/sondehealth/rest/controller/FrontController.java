package com.sondehealth.rest.controller;

import java.net.URISyntaxException;

import javax.security.auth.message.AuthException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.sondehealth.model.Constants;
import com.sondehealth.model.OAuthRequestBody;
import com.sondehealth.model.OAuthResponse;
import com.sondehealth.model.OAuthTokenResponse;
import com.sondehealth.model.SubjectCreationRequest;
import com.sondehealth.services.AuthTokenService;
import com.sondehealth.services.UtilityService;

@CrossOrigin(maxAge = 3600)
@RestController
public class FrontController {
	
	@Autowired
	UtilityService utilityService;
	
	@Autowired
	AuthTokenService authTokenService;
	
	Logger logger = LoggerFactory.getLogger(FrontController.class);
	
	@PostMapping("/oauth2/token")
	public ResponseEntity<Object> getOauthToken() throws RestClientException, URISyntaxException, AuthException, JsonProcessingException
	{
		String encodedCreds = utilityService.base64Encode(Constants.clientId, Constants.clientSecret);
		String basicAuthToken = "Basic "+ encodedCreds;
		OAuthRequestBody body = new OAuthRequestBody();
	    body.setGrant_type("client_credentials");
		body.setScope("sonde-platform/storage.write sonde-platform/scores.write sonde-platform/measures.read sonde-platform/measures.list");
		OAuthResponse response = authTokenService.getOauthToken(basicAuthToken, body);
		return new ResponseEntity<Object>(response.getResponse(), HttpStatus.valueOf(response.getStatusCode()));
	}
	
	@PostMapping("/platform/v1/users")
	public ResponseEntity<Object> createUser(@RequestBody SubjectCreationRequest subjectCreationRequest) throws AuthException, JsonProcessingException{
		
		String encodedCreds = utilityService.base64Encode(Constants.clientId, Constants.clientSecret);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String basicAuthToken = "Basic "+ encodedCreds;
		OAuthRequestBody body = new OAuthRequestBody();
	    body.setGrant_type("client_credentials");
		body.setScope("sonde-platform/users.write");
		OAuthResponse oauthResponse = authTokenService.getOauthToken(basicAuthToken, body);
		OAuthTokenResponse resp = null;
		String json = gson.toJson(oauthResponse.getResponse());
		resp = gson.fromJson(json, OAuthTokenResponse.class);
		
		Object response = null;
		int statusCode = 200;
		ObjectMapper objMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
		HttpRequestWithBody http = Unirest.post(Constants.subjectAPIURL);
		try {
	        HttpResponse<String> httpResponse = http
	                .header("Content-Type", "application/json")
	                .header("Authorization", resp.getAccess_token())
	                .body(objMapper.writeValueAsString(subjectCreationRequest))
	                .asString();
	        statusCode = httpResponse.getStatus();
	        String responseBody = httpResponse.getBody();
	        logger.info("Response status code - "+ statusCode + "Response body - " + responseBody);
	        response = gson.fromJson(httpResponse.getBody(), Object.class);
	    } catch (UnirestException e) {
	        e.printStackTrace();
	        throw new AuthException("Unirest error");
	    }
		return new ResponseEntity<Object>(response,HttpStatus.valueOf(statusCode));
	}

}
