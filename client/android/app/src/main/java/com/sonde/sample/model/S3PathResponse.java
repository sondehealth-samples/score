
package com.sonde.sample.model;

import com.google.gson.annotations.Expose;


public class S3PathResponse {

    @Expose
    private String filePath;
    @Expose
    private String requestId;
    @Expose
    private String signedURL;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSignedURL() {
        return signedURL;
    }

    public void setSignedURL(String signedURL) {
        this.signedURL = signedURL;
    }

}
