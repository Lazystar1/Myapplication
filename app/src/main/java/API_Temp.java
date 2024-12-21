
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class API_Temp extends Activity {

    private static final String BASE_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/api/";

    public static String SetURL(String URLVal) {
        return BASE_URL + URLVal;
    }

    // Interface for callback
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    // Method to execute a GET request with callback
    public void executeGetRequest(String endpoint, ApiCallback callback) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                HttpURLConnection connection = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(SetURL(endpoint));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return result.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response != null) {
                    callback.onSuccess(response);
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute();
    }

    // Method to execute a POST request (Insert)
    public String executePostRequest(String endpoint, String jsonInput) {
        HttpURLConnection connection = null;
        OutputStream os = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(SetURL(endpoint));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            os = connection.getOutputStream();
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line.trim());
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Method to execute a DELETE request
    public String executeDeleteRequest(String endpoint, String id) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(SetURL(endpoint) + "/" + id);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Example usage in an AsyncTask for network operations
    private class ApiTask extends AsyncTask<String, Void, String> {

        private String operationType;
        private String jsonInput;

        public ApiTask(String operationType, String jsonInput) {
            this.operationType = operationType;
            this.jsonInput = jsonInput;
        }

        @Override
        protected String doInBackground(String... params) {
            String endpoint = params[0];
            switch (operationType) {
                case "GET":
                    //return executeGetRequest(endpoint);
                case "POST":
                    return executePostRequest(endpoint, jsonInput);
                case "DELETE":
                    return executeDeleteRequest(endpoint, jsonInput);  // Assuming the ID is passed as jsonInput for DELETE
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(API_Temp.this, "Response: " + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(API_Temp.this, "Error in API request", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Example of how to call these methods
    public void testApiCalls() {
        // GET request
        new ApiTask("GET", null).execute("Tank/GetCityDetails");

        // POST request (insert data)
        String jsonToInsert = "{\"name\":\"Example\", \"value\":\"12345\"}";
        new ApiTask("POST", jsonToInsert).execute("Tank/InsertData");

        // DELETE request (delete based on ID)
        String idToDelete = "12345";
        new ApiTask("DELETE", idToDelete).execute("Tank/DeleteData");
    }
}
