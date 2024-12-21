package com.example.new_iwdms;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API_TEMP extends Activity {

    public  String baseUrl = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/";
    public  String mobilebaseUrl ="https://test.seminalsoftwarepvt.in/VijaypuraMiddleware/api/";
    public  String mobilemethod="Water/updateMobileonuserid";
    public String getMethod = "ValveDetails/GetValveId";
    public String postMethod = "ValveDetails/InsertValveDEtails?";
    public String postscheduleMethod = "ScheduleDetails/InsertScheduleDetails?";
    public String getlogin = "LoginApi/Loginauth";
    public  String GetValvemen="ScheduleDetails/GetValveMenName";
    public String deleteMethod = "ValveDetails/DeleteData";
    public String GetNotALerts="NotificationDetails/GetAlerts";
    public String postnotification="NotificationDetails/InsertNotificationDetails?";
    private OkHttpClient client = new OkHttpClient();

    // Interface for callback
    public interface ApiCallback {
        void onSuccess(String response) throws JSONException;
        void onError(String error);
    }

    // Method to execute a GET request with dynamic base URL and endpoint
    public void executeGetRequest(String baseUrl, String endpoint, ApiCallback callback) {
        String url = baseUrl + endpoint;
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError("Failed to fetch data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> callback.onError("Failed to fetch data: " + response.message()));
                }
            }
        });
    }

    // Method to execute a POST request with dynamic base URL and endpoint
    public void executePostRequest(String baseUrl, String endpoint, String urlParams, ApiCallback callback) {
        String url = baseUrl + endpoint;

        // Log the full URL
        Log.d("API Request", "Full API URL: " + url + "?" + urlParams);

        // Build the request body with URL-encoded parameters
        RequestBody body = RequestBody.create(urlParams, okhttp3.MediaType.parse("application/x-www-form-urlencoded"));

        Request request = new Request.Builder()
                .url(url) // Base URL without parameters in the URL
                .post(body) // Pass the parameters in the body
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError("Failed to post data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> callback.onError("Error: " + response.code() + " - " + response.message()));
                }
            }
        });
    }

    public void executePutRequest(String baseUrl, String endpoint, String urlParams, ApiCallback callback) {
        String url = baseUrl + endpoint;

        // Create the request body with URL-encoded parameters
        RequestBody body = RequestBody.create(urlParams, okhttp3.MediaType.parse("application/x-www-form-urlencoded"));

        // Build the HTTP PUT request
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError("Failed to update data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> callback.onError("Failed to update data: " + response.message()));
                }
            }
        });
    }


    // Method to execute a DELETE request with dynamic base URL and endpoint
    public void executeDeleteRequest(String baseUrl, String endpoint, String id, ApiCallback callback) {
        String url = baseUrl + endpoint + "/" + id;
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError("Failed to delete data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> callback.onError("Failed to delete data: " + response.message()));
                }
            }
        });
    }
    // Method to execute a GET request with query parameters
    public void executeGetRequestonp(String baseUrl, String endpoint, Map<String, String> queryParams, ApiCallback callback) {
        // Build the URL with query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + endpoint).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onError("Failed to fetch data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            callback.onSuccess(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    runOnUiThread(() -> callback.onError("Failed to fetch data: " + response.message()));
                }
            }
        });
    }

    // Example usage of the API calls
    public void testApiCalls() {
        String baseUrl = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/";
        String getMethod = "ValveDetails/GetValveId";
        String postMethod = "ValveDetails/InsertData";
        String deleteMethod = "ValveDetails/DeleteData";

        // GET request
        executeGetRequest(baseUrl, getMethod, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(API_TEMP.this, "GET Response: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(API_TEMP.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // POST request (insert data)
        String urlParams = "name=Example&value=12345";
        executePostRequest(baseUrl, postMethod, urlParams, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(API_TEMP.this, "POST Response: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(API_TEMP.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // DELETE request (delete based on ID)
        String idToDelete = "12345";
        executeDeleteRequest(baseUrl, deleteMethod, idToDelete, new ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(API_TEMP.this, "DELETE Response: " + response, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(API_TEMP.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
