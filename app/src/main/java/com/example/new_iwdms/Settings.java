package com.example.new_iwdms;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import dmax.dialog.SpotsDialog;


public class Settings extends Fragment {
    private EditText etOldmobile,etnewmobile;
    private Button btnupdate;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Settings() {
        // Required empty public constructor
    }
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Settings");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment only once
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Bind the EditTexts to their IDs
        etOldmobile = view.findViewById(R.id.etoldmob);
        etnewmobile = view.findViewById(R.id.ednewmob);
        btnupdate=view.findViewById(R.id.btnupdate);

        if (etOldmobile != null) {
            // Retrieve the saved mobile number from SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String storedMobileNumber = sharedPreferences.getString("userMobileNo", ""); // Default is empty string if not found

            // Set the retrieved mobile number to the EditText
            etOldmobile.setText(storedMobileNumber); // Set the mobile number in the EditText
        } else {
            Log.e("TAG", "Mobile Number Not found.");
        }

        // Check for network connectivity
        if (!isNetworkConnected()) {
            showNoInternetDialog();
        }

        btnupdate.setOnClickListener(v -> {
            try {
                new UpdateMobileNumber().execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Return the inflated view
        return view;
    }


    private class UpdateMobileNumber extends AsyncTask<Void, Void, String> {
        private android.app.AlertDialog dialog;


        @Override
        protected void onPreExecute() {
            // Initialize the SpotsDialog with the correct usage
            dialog = new SpotsDialog.Builder()
                    .setContext(getContext()) // Pass the context here
                    .setMessage("Updating Mobile Number...") // Optional, you can set your custom message
                    .setCancelable(false) // Optional, if you want the dialog to be non-cancelable
                    .build();
            dialog.show(); // Show the dialog
        }


        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = "";

            String oldMobile = etOldmobile.getText().toString();
            String newMobile = etnewmobile.getText().toString();

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            int storedUserId = sharedPreferences.getInt("userId", 0);

            try {
                String apiUrl = "https://test.seminalsoftwarepvt.in/VijaypuraMiddleware/api/Water/updateMobileonuserid?";
                String urlString = apiUrl + "userId=" + storedUserId + "&current_mobile=" + oldMobile + "&userMobileNo=" + newMobile;

                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");

                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    result = "200";
                } else if (responseCode == 202) {
                    result = "202";
                } else {
                    result = "Error: " + responseCode;
                }

            } catch (IOException e) {
                e.printStackTrace();
                result = "Error: " + e.getMessage();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            switch (result) {
                case "200":
                    Toast.makeText(getContext(), "Mobile number updated successfully", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("newMobile", etnewmobile.getText().toString());
                    editor.apply();
                    break;

                case "202":
                    showAlert("Invalid Details", "The current mobile number is incorrect.");
                    break;

                default:
                    showAlert("Error", "An unexpected error occurred. Please try again.");
                    break;
            }
        }

        private void showAlert(String title, String message) {
            new AlertDialog.Builder(getContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    }








    private void updpatenumber() {
        String API = "https://test.seminalsoftwarepvt.in/VijaypuraMiddleware/api/Water/updateMobileonuserid";
        String oldMobNum = etOldmobile.getText().toString();
        String newMobNum = etnewmobile.getText().toString();

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int storedUserId = sharedPreferences.getInt("userId", 0); // Default to 0 if not found

        // Create a separate thread to handle network operations
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Construct the complete URL
                    URL url = new URL(API);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    // Set request method to POST
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true); // Enable output for sending parameters
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Construct the request body
                    String postParams = "userId=" + storedUserId +
                            "&current_mobile=" + oldMobNum +
                            "&userMobileNo=" + newMobNum;

                    // Write the POST data to the output stream
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(postParams);
                    writer.flush();
                    writer.close();
                    os.close();

                    // Get the response code
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Read the response from the server
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // Handle the successful response
                        String serverResponse = response.toString();
                        Log.d("API Response", serverResponse);
                        // Process the server response (e.g., update UI or notify user)
                    } else {
                        // Handle error cases
                        Log.e("API Error", "Error Response Code: " + responseCode);
                    }

                    // Disconnect the connection
                    urlConnection.disconnect();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("API Error", "Exception occurred: " + e.getMessage());
                }
            }
        }).start();
    }



    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showNoInternetDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK", (dialog, which) -> {

                    requireActivity().finish();
                })
                .show();
        }

    }