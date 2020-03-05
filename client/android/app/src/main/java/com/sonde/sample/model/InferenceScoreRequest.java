
package com.sonde.sample.model;

import com.google.gson.annotations.Expose;


public class InferenceScoreRequest {

    @Expose
    private String fileLocation;
    @Expose
    private String measureName;
    @Expose
    private String subjectIdentifier;

    public InferenceScoreRequest(String fileLocation, String measureName, String subjectIdentifier) {
        this.fileLocation = fileLocation;
        this.measureName = measureName;
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getFileLocation() {
        return fileLocation;
    }
    public String getMeasureName() {
        return measureName;
    }
    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }


}
