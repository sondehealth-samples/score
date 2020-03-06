package com.sonde.sample.model;

import com.google.gson.annotations.Expose;

public class S3FilePathRequest {
    @Expose
    private String fileType;
    @Expose
    private String countryCode;
    @Expose
    private String subjectIdentifier;

    public S3FilePathRequest(String fileType, String countryCode, String subjectIdentifier) {
        this.fileType = fileType;
        this.countryCode = countryCode;
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getFileType() {
        return fileType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }
}
