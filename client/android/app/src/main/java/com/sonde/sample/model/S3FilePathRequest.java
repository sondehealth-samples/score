package com.sonde.sample.model;

import com.google.gson.annotations.Expose;

public class S3FilePathRequest {
    @Expose
    private String fileType;
    @Expose
    private String countryCode;
    @Expose
    private String userIdentifier;

    public S3FilePathRequest(String fileType, String countryCode, String userIdentifier) {
        this.fileType = fileType;
        this.countryCode = countryCode;
        this.userIdentifier = userIdentifier;
    }

    public String getFileType() {
        return fileType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }
}
