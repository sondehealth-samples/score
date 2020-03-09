package com.sondehealth.apiserver.services.impl;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sondehealth.apiserver.services.UtilityService;

@Service
public class UtilityServiceImpl implements UtilityService {

	@Override
	public String base64Encode(String clientId, String clientSecret) {
		String originalInput = clientId + ":" + clientSecret;
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		return encodedString;
	}
	
	public static void addEnvProperties(Map<String, String> properties){
		try{
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
		   // Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
		    theCaseInsensitiveEnvironmentField.setAccessible(true);
		    Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
		    cienv.putAll(properties);	
		}
		catch(Exception ex){
			
		}
	}

}
