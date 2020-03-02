
package com.sonde.sample.model;

import com.google.gson.annotations.SerializedName;


public class InferenceScoreResponse {

    @SerializedName("measureId")
    private Long mMeasureId;
    @SerializedName("name")
    private String mName;
    @SerializedName("requestId")
    private String mRequestId;
    @SerializedName("score")
    private Long mScore;
    @SerializedName("scoreId")
    private String mScoreId;

    public Long getMeasureId() {
        return mMeasureId;
    }

    public void setMeasureId(Long measureId) {
        mMeasureId = measureId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(String requestId) {
        mRequestId = requestId;
    }

    public Long getScore() {
        return mScore;
    }

    public void setScore(Long score) {
        mScore = score;
    }

    public String getScoreId() {
        return mScoreId;
    }

    public void setScoreId(String scoreId) {
        mScoreId = scoreId;
    }

}
