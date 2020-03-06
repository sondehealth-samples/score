package com.sonde.sample;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.sonde.sample.model.Measure;
import com.sonde.sample.model.MeasureResponse;
import com.sonde.sample.model.S3FilePathRequest;
import com.sonde.sample.model.S3PathResponse;
import com.sonde.sample.service.BackendApi;
import com.sonde.sample.service.RetrofitClientInstance;
import com.sonde.sample.utils.Configuration;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final int RECORD_REQUEST_CODE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String userIdentifier = "#######"; // replace with your subject id

    private ProgressDialog mProgressDialog;
    private String accessToken;
    private List<Measure> measureList;

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
        getAccessToken(Configuration.ACCESS_TOKEN_URL);
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


    private void getAccessToken(String apiUrl) {
        mProgressDialog.setMessage("Loading....");
        mProgressDialog.show();
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        Call<AccessTokenResponse> call = backendApi.getAuthToken(apiUrl);
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                Log.i(TAG, "AccessToken Response : " + response.toString());
                AccessTokenResponse accessTokenResponse = response.body();
                if (accessTokenResponse != null) {
                    Log.i(TAG, "AccessToken is : " + accessTokenResponse.getAccessToken());
                    accessToken = accessTokenResponse.getAccessToken();
                    getMeasures();
                } else {
                    dismissDialog();
                    Log.e(TAG, "error: code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                Log.e(TAG, "error: " + t);
                dismissDialog();
            }
        });
    }

    private void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void getMeasures() {
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        Call<MeasureResponse> call = backendApi.getMeasures(accessToken);
        call.enqueue(new Callback<MeasureResponse>() {
            @Override
            public void onResponse(Call<MeasureResponse> call, Response<MeasureResponse> response) {
                dismissDialog();
                MeasureResponse measureResponse = response.body();
                if (measureResponse != null) {
                    measureList = measureResponse.getMeasures();
                } else {
                    Log.e(TAG, "error: code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MeasureResponse> call, Throwable t) {
                Log.e(TAG, "error: " + t);
                dismissDialog();
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
        String countryCode = "IN";

        Call<S3PathResponse> call = backendApi.getS3FilePath(accessToken, new S3FilePathRequest("wav", countryCode, userIdentifier));
        call.enqueue(new Callback<S3PathResponse>() {
            @Override
            public void onResponse(Call<S3PathResponse> call, Response<S3PathResponse> response) {
                S3PathResponse s3PathResponse = response.body();
                if (s3PathResponse != null) {
                    uploadFileToS3(s3PathResponse, filePath);
                } else {
                    Log.e(TAG, "error: code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<S3PathResponse> call, Throwable t) {
                Log.e(TAG, "error: " + t);
                dismissDialog();
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
                    requestForMeasureScore(s3PathResponse.getFilePath(), measureList.get(0).getName(), userIdentifier);
                } else {
                    Log.e(TAG, " : Failed to upload file " + filePath.getName() + " Error code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, " : Failed to upload file " + filePath.getName() + " Error: " + t);
                dismissDialog();
            }
        });
    }

    private void requestForMeasureScore(String fileLocation, final String measureName, String subjectIdentifier) {
        Log.i(TAG, " : fileLocation : " + fileLocation);
        BackendApi backendApi = RetrofitClientInstance.getRetrofitInstance().create(BackendApi.class);
        Call<InferenceScoreResponse> call = backendApi.getInferenceScore(accessToken, new InferenceScoreRequest(fileLocation, measureName, subjectIdentifier));
        call.enqueue(new Callback<InferenceScoreResponse>() {
            @Override
            public void onResponse(Call<InferenceScoreResponse> call, Response<InferenceScoreResponse> response) {
                dismissDialog();
                InferenceScoreResponse scoreResponse = response.body();
                if (scoreResponse != null) {
                    showScoreDialog(measureName, scoreResponse.getScore());
                } else {
                    Log.e(TAG, " : Failed to get score , Error: " + response.code());
                    Toast.makeText(MainActivity.this, "Could not calculate the score, please try again", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<InferenceScoreResponse> call, Throwable t) {
                Log.e(TAG, " : Failed to get score , Error: " + t);
                dismissDialog();
                Toast.makeText(MainActivity.this, "Could not calculate the score, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showScoreDialog(String measureName, double score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Measure score")
                .setMessage("Measure name : " + measureName + " \n Score : " + score)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}
