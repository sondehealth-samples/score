package com.sondehealth.services.impl;

import java.util.Base64;

import org.springframework.stereotype.Service;

import com.sondehealth.services.UtilityService;

@Service
public class UtilityServiceImpl implements UtilityService {

	@Override
	public String base64Encode(String clientId, String clientSecret) {
		String originalInput = clientId + ":" + clientSecret;
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		return encodedString;
	}

}
