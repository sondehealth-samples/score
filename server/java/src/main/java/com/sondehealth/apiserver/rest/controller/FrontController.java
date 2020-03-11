package com.sondehealth.apiserver.rest.controller;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sondehealth.apiserver.model.Constants;
import com.sondehealth.apiserver.model.OAuthResponseDTO;
import com.sondehealth.apiserver.model.SubjectCreationRequest;
import com.sondehealth.apiserver.services.UtilityService;
import com.sondehealth.authentication.AccessToken;
import com.sondehealth.authentication.SondeCredentialsService;
import com.sondehealth.exceptions.SDKClientException;
import com.sondehealth.exceptions.SDKUnauthorizedException;
import com.sondehealth.exceptions.SondeServiceException;
import com.sondehealth.factory.SondeHealthClientProvider;
import com.sondehealth.model.Gender;
import com.sondehealth.model.Scopes;
import com.sondehealth.model.UserCreationRequest;
import com.sondehealth.model.UserCreationResponse;
import com.sondehealth.service.SubjectClient;

@CrossOrigin(maxAge = 3600)
@RestController
public class FrontController {
	
	@Autowired
	UtilityService utilityService;
	
	Logger logger = LoggerFactory.getLogger(FrontController.class);
	
	@PostMapping("/oauth2/token")
	public ResponseEntity<Object> getOauthToken() throws RestClientException, URISyntaxException, AuthException, JsonProcessingException
	{	
		SondeCredentialsService cred = SondeHealthClientProvider.getClientCredentialsAuthProvider(Constants.clientId, Constants.clientSecret);
		List<Scopes> scopeList = Arrays.asList(Scopes.STORAGE_WRITE, Scopes.SCORES_WRITE, Scopes.MEASURES_READ, Scopes.MEASURES_LIST);
		AccessToken token = null;
		OAuthResponseDTO response = null;
		try{
			token = cred.generateAccessToken(scopeList);
			response = new OAuthResponseDTO();
			response.setAccess_token(token.getAccessToken());
			response.setExpires_in(token.getExpiresIn());
			response.setToken_type(token.getTokenType());
		}
		catch(SondeServiceException | SDKClientException | SDKUnauthorizedException ex){
			logger.error("Error while generating token");
			ex.printStackTrace();
			throw ex;
		}
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	@PostMapping("/platform/v1/users")
	public ResponseEntity<Object> createUser(@RequestBody SubjectCreationRequest subjectCreationRequest) throws AuthException, JsonProcessingException{
		SondeCredentialsService cred = SondeHealthClientProvider.getClientCredentialsAuthProvider(Constants.clientId, Constants.clientSecret); 
		UserCreationRequest request = new UserCreationRequest.UserBuilder(Gender.valueOf(subjectCreationRequest.getGender()), 
				subjectCreationRequest.getYearOfBirth()).withLanguage(subjectCreationRequest.getLanguage()).build();
		
		UserCreationResponse response;
		try{
			SubjectClient subjectClient = SondeHealthClientProvider.getSubjectClient(cred);
			response = subjectClient.createSubject(request);
		}
		catch(SondeServiceException | SDKClientException | SDKUnauthorizedException ex){
			logger.error("Error while generating token");
			ex.printStackTrace();
			throw ex;
		}
		return new ResponseEntity<Object>(response, HttpStatus.OK);
		
		
	}

}
