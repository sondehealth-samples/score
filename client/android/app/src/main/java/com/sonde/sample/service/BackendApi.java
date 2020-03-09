package com.sonde.sample.service;

import com.sonde.sample.model.AccessTokenResponse;
import com.sonde.sample.model.InferenceScoreRequest;
import com.sonde.sample.model.InferenceScoreResponse;
import com.sonde.sample.model.MeasureResponse;
import com.sonde.sample.model.S3FilePathRequest;
import com.sonde.sample.model.S3PathResponse;
import com.sonde.sample.model.SignUpRequest;
import com.sonde.sample.model.SignUpResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public interface BackendApi {
    @POST
    Call<AccessTokenResponse> getAuthToken(@Url String url);

    @POST
    Call<SignUpResponse> signUpUser(@Url String url, @Body SignUpRequest signUpRequest);

    @GET("measures")
    Call<MeasureResponse> getMeasures(@Header("Authorization") String authToken);

    @POST("storage/files")
    Call<S3PathResponse> getS3FilePath(@Header("Authorization") String authToken, @Body S3FilePathRequest request);

    @PUT
    Call<ResponseBody> uploadFileToS3(@Url String url, @Body RequestBody requestBody);

    @POST("inference/scores")
    Call<InferenceScoreResponse> getInferenceScore(@Header("Authorization") String authToken, @Body InferenceScoreRequest inferenceScoreRequest);



}
