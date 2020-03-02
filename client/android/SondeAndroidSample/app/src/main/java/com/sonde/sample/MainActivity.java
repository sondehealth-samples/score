package com.sonde.sample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sonde.sample.model.AccessTokenResponse;
import com.sonde.sample.model.InferenceScoreRequest;
import com.sonde.sample.model.InferenceScoreResponse;
import com.sonde.sample.model.S3FilePathRequest;
import com.sonde.sample.model.S3PathResponse;
import com.sonde.sample.service.BackendApi;
import com.sonde.sample.service.RetrofitClientInstance;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private String subjectIdentifier = "subject1"; // replace with your subject id
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        Button btnStart = findViewById(R.id.btn_record);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RecordingActivity.class);
                startActivityForResult(intent, RECORD_REQUEST_CODE);
            }
        });
        getAuthToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    uploadFile(data.getStringExtra("filePath"));
                }
            } else {
                Toast.makeText(this, "Unable to generate audio file", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void getAuthToken() {
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.show();
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        String authUrl = "http://172.24.24.47:8080/oauth2/token"; // use your backed api to get access token
        Call<AccessTokenResponse> call = backendApi.getAuthToken(authUrl);
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                mProgressDialog.dismiss();
                Log.i(TAG, "AccessToken Response : " + response.toString());
                AccessTokenResponse accessTokenResponse = response.body();
                if (accessTokenResponse != null) {
                    accessToken = accessTokenResponse.getAccessToken();
                } else {
                    Log.e(TAG, "error: code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e(TAG, "error: " + t);
                mProgressDialog.dismiss();
            }
        });
    }


    private void uploadFile(String filePath) {
        mProgressDialog.setMessage("Uploading....");
        mProgressDialog.show();
        File file = new File(filePath);
        getS3SignedUrl(file);
    }

    private void getS3SignedUrl(final File filePath) {
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);

        Call<S3PathResponse> call = backendApi.getS3FilePath(accessToken, new S3FilePathRequest("wav", "IN", subjectIdentifier));
        call.enqueue(new Callback<S3PathResponse>() {
            @Override
            public void onResponse(Call<S3PathResponse> call, Response<S3PathResponse> response) {
                S3PathResponse s3PathResponse = response.body();
                if (s3PathResponse != null) {
                    s3PathResponse.setSignedURL("https://dev-sondeplatform-in-subject-metadata.s3.amazonaws.com/75c6065d-608e-4a1a-84fb-13afe877ca09/voice-samples/810f618d-cc61-4b4b-92f8-81c0abd4a235.wav?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIAY4433OL7S3KMKEQS%2F20200225%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Date=20200225T134159Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEMb%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIFLXdpbODumXLCsE9Z%2B1K3gMCaQtA2nOPaHuY0%2BamMeRAiBflkx5QqSeCmN9Ug3dw1SJ8oEQBJ0T8Vl4VExL%2B6JIoyqEAgiP%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAEaDDYxMTgyMjgyNjIzOSIMYNvbwJOroOYvnScNKtgB0wr9L5QiIkQmAFT8LNsVXr8jckxHo24R%2FiR2dQw8VfdrYOfVLcnPslQKt40Yoa9DBbRDZu1xf50XCR2ZIS8jqmarGMzpEiyRXRstOIorBdPlgHKF43ITWQmQPWUuKUDkmbvCg7dmAoz6kPk5tEPdTbRzLwVcIqYBOr%2FOE8%2FnAHsIbvctb7MIDuCYPPpCAIuv1csVeZu0E9XgUWoIJqxOZHxTcZk6zGvI3aCK7FSkg4SReT%2BFPr%2FkT18by9T7%2Fi3RS8dxU5g4ZVonIDfFHq7ZRLvTTH9uGfX2MKXI1PIFOuEBIsR7nba6R9nSkUFfyFHRjCePlAvaUUV9lRcHjrHhVb0ygFcy%2FXgLcpaYuSbNuz11YZjFpGufsb5X5pp4zYJ%2F8s7t%2B1XuWJYBxhh6Nj3EEyQM7uKKYOuWyGkoMt%2FyU4kN%2BC691cceFQ4E6cz41vMtBn0CkAT2mdq%2FMc8GJKMBEF27KXJs4lH2gS0i66F7sVvboAviNX5KzzZpNHaUkJ8xuOJJcEO5HIqRyJkp86ssfFzE9l55ctSsG3XNDYLqctJiLO6GKGS%2B6W9AcBKsZ5Qc0qIHqrvi%2B%2B6hjPBPq9fKt%2By4&X-Amz-Signature=7bf8db452aed989c0df6ab38fc5ec8fff1df80259a2894f4dc2b46d9c72e1856");
                    s3PathResponse.setFileLocation("s3://dev-sondeplatform-in-subject-metadata/75c6065d-608e-4a1a-84fb-13afe877ca09/voice-samples/810f618d-cc61-4b4b-92f8-81c0abd4a235.wav");
                    uploadFileToS3(s3PathResponse, filePath);
                } else {
                    Log.e(TAG, "error: code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<S3PathResponse> call, Throwable t) {
                Log.e(TAG, "error: " + t);
                mProgressDialog.dismiss();
            }
        });
    }

    private void uploadFileToS3(final S3PathResponse s3PathResponse, final File filePath) {
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream");
        String uploadUrl = s3PathResponse.getSignedURL();
        Call<ResponseBody> call = backendApi.uploadFileToS3(uploadUrl, RequestBody.create(MEDIA_TYPE_OCTET_STREAM, filePath));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, " : File Uploaded successfully " + filePath.getName());
                    requestForMeasureScore(s3PathResponse.getFileLocation(), "nasality", subjectIdentifier);
                } else {
                    Log.e(TAG, " : Failed to upload file " + filePath.getName() + " Error code : "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, " : Failed to upload file " + filePath.getName() + " Error: " + t);
                requestForMeasureScore(s3PathResponse.getFileLocation(), "nasality", subjectIdentifier);
            }
        });
    }

    private void requestForMeasureScore(String fileLocation, String measureName, String subjectIdentifier) {
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        Call<InferenceScoreResponse> call = backendApi.getInferenceScore(accessToken, new InferenceScoreRequest(fileLocation, measureName, subjectIdentifier));
        call.enqueue(new Callback<InferenceScoreResponse>() {
            @Override
            public void onResponse(Call<InferenceScoreResponse> call, Response<InferenceScoreResponse> response) {
                mProgressDialog.dismiss();
                InferenceScoreResponse scoreResponse = response.body();
                if (scoreResponse != null) {
                    Toast.makeText(MainActivity.this, "Your score is " + scoreResponse.getScore(), Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, " : Failed to get score , Error: " + response.code());
                    Toast.makeText(MainActivity.this, "Could not calculate the score, please try again" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<InferenceScoreResponse> call, Throwable t) {
                Log.e(TAG, " : Failed to get score , Error: " + t);
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Could not calculate the score, please try again" , Toast.LENGTH_LONG).show();
            }
        });
    }
}
