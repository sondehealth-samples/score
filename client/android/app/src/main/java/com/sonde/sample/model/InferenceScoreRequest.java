
package com.sonde.sample.model;

import com.google.gson.annotations.Expose;


public class InferenceScoreRequest {

    @Expose
    private String filePath;
    @Expose
    private String measureName;
    @Expose
    private String userIdentifier;

    public InferenceScoreRequest(String filePath, String measureName, String userIdentifier) {
        this.filePath = filePath;
        this.measureName = measureName;
        this.userIdentifier = userIdentifier;
    }

    public String getFilePath() {
        return filePath;
    }
    public String getMeasureName() {
        return measureName;
    }
    public String getUserIdentifier() {
        return userIdentifier;
    }

}
