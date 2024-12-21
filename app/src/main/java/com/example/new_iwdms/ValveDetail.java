package com.example.new_iwdms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ValveDetail extends Fragment {
    private long startTime;
    private TextView tvDuration;
    private long endTime;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etDuration;
    private boolean isTimerRunning = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 3;
    private TextView tvInstruction;
    private ImageView imgPhoto;
    private ImageView imgCamera;
    private ImageView imgDelete;
    private Spinner valveIdSpinner;
    private Bitmap capturedImageBitmap;


    public ValveDetail() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null && getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.getSupportActionBar().setTitle("Valve Detail");
        }

        // Check internet connection
        if (!isNetworkConnected()) {
            showNoInternetDialog();
            return inflater.inflate(R.layout.fragment_valve_detail, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_valve_detail, container, false);
        SharedPreferences sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);

        valveIdSpinner = view.findViewById(R.id.spnvalveid);
        populateValveIdSpinner();

        etStartTime = view.findViewById(R.id.et_start_time);
        etEndTime = view.findViewById(R.id.et_endtime);
        etDuration = view.findViewById(R.id.et_duration);
        tvDuration = view.findViewById(R.id.tv_duration);
        Button btnStart = view.findViewById(R.id.btn_start);
        Button btnEnd = view.findViewById(R.id.btn_end);
        Button btnSubmit = view.findViewById(R.id.btnsubmit);
        imgPhoto = view.findViewById(R.id.img_photo);
        imgCamera = view.findViewById(R.id.img_camera);
        imgDelete = view.findViewById(R.id.img_delete);
        tvInstruction = view.findViewById(R.id.tv_instruction);

        // Initialize Handler
        handler = new Handler();

        // Runnable to update the current time every second
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the current time in the EditText
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                String currentTime = sdf.format(Calendar.getInstance().getTime());
                etStartTime.setText(currentTime);
                // Repeat this runnable code block every second
                handler.postDelayed(this, 1000);
            }
        };

        // Set up Start button
        btnStart.setOnClickListener(v -> {
            if (!isTimerRunning) {
                // Start updating current time
                handler.post(updateTimeRunnable);

                // Record the start time
                startTime = Calendar.getInstance().getTimeInMillis();
                isTimerRunning = true;
            }
        });

        // Set up End button
        btnEnd.setOnClickListener(v -> {
            if (isTimerRunning) {
                // Stop updating current time
                handler.removeCallbacks(updateTimeRunnable);

                // Record the end time
                endTime = Calendar.getInstance().getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                String endTimeFormatted = sdf.format(Calendar.getInstance().getTime());
                etEndTime.setText(endTimeFormatted);

                // Calculate the duration
                long durationMillis = endTime - startTime;
                long durationSeconds = durationMillis / 1000;
                long minutes = durationSeconds / 60;
                long seconds = durationSeconds % 60;
                String durationFormatted = String.format("%d min %d sec", minutes, seconds);

                // Show the duration TextView and set its text
                tvDuration.setText(durationFormatted);
                tvDuration.setVisibility(View.VISIBLE);

                // Stop the timer
                isTimerRunning = false;
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    InsertData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSIONS);
            } else {
                dispatchTakePictureIntent();
            }
        });

        // Set up Delete button
        imgDelete.setOnClickListener(v -> {
            imgPhoto.setImageDrawable(null);
            imgPhoto.setVisibility(View.GONE);
            imgCamera.setVisibility(View.VISIBLE);
            imgDelete.setVisibility(View.GONE);
            tvInstruction.setVisibility(View.VISIBLE);
        });

        return view;
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
                    // Close the application or navigate to another screen
                    requireActivity().finish(); // Or use other navigation logic
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(getContext(), "No camera app available", Toast.LENGTH_SHORT).show();
                Log.d("ValveDetail", "No camera app available");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    capturedImageBitmap = imageBitmap;
                    imgPhoto.setImageBitmap(imageBitmap);
                    imgPhoto.setVisibility(View.VISIBLE);
                    imgCamera.setVisibility(View.GONE);
                    imgDelete.setVisibility(View.VISIBLE);
                    tvInstruction.setVisibility(View.GONE); // Hide the instruction text after capturing photo
                }
            }
        } else {
            Toast.makeText(getContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
            Log.d("ValveDetail", "Failed to capture image");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                Log.d("ValveDetail", "Camera permission denied");
            }
        }
    }

    private void InsertData() {
        API_TEMP apiTemp = new API_TEMP();
        String selectedValveId = valveIdSpinner.getSelectedItem().toString();
        String base64Image = encodeImageToBase64(capturedImageBitmap);
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();
        String duration = tvDuration.getText().toString();

        SharedPreferences sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
        String latitude = sharedPref.getString("Latitude", "default_latitude");
        String longitude = sharedPref.getString("Longitude", "default_longitude");

        String urlParams =  "Valve_Unique_Id=" + selectedValveId +
                "&Image=" + base64Image +
                "&StartTime=" + startTime +
                "&EndTime=" + endTime +
                "&Duration=" + duration +
                "&Latitude=" + latitude +
                "&Longitude=" + longitude;

        // Execute POST request
        apiTemp.executePostRequest(apiTemp.baseUrl,apiTemp.postMethod, urlParams, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(), "Inserted Successfully" ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed To Insert " , Toast.LENGTH_LONG).show();
            }
        });
    }


private String encodeImageToBase64(Bitmap imageBitmap) {
    if (imageBitmap == null) return null;

    // Resize image if necessary
    int width = imageBitmap.getWidth();
    int height = imageBitmap.getHeight();
    int maxSize = 1024; // Example max size
    if (width > maxSize || height > maxSize) {
        float ratio = (float) width / height;
        if (width > height) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, true);
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream); // Adjust quality

    byte[] imageBytes = byteArrayOutputStream.toByteArray();
    return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
}

    private boolean validateInputs() {
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String selectedValveId = valveIdSpinner.getSelectedItem().toString();

        if (selectedValveId.equals("Select")) {
            showValidationDialog("Validation Error", "Please select a valid Valve ID.");
            return false;
        }
        if (startTime.isEmpty()) {
            showValidationDialog("Validation Error", "Start time cannot be empty.");
            return false;
        }
        if (endTime.isEmpty()) {
            showValidationDialog("Validation Error", "End time cannot be empty.");
            return false;
        }
        if (capturedImageBitmap == null) {
            showValidationDialog("Validation Error", "Please capture an image before submitting.");
            return false;
        }
        return true;
    }

    // Validation popup dialog
    private void showValidationDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void populateValveIdSpinner() {
        API_TEMP apiTemp = new API_TEMP();

        apiTemp.executeGetRequest(apiTemp.baseUrl,apiTemp.getMethod, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                // Parse the JSON response and set it to the Spinner
                List<String> valveIds = parseValveIds(response);

                // Add "Select" as the first item
                valveIds.add(0, "Select");

                // Create an ArrayAdapter using the valveIds list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, valveIds);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Set the adapter to the Spinner
                valveIdSpinner.setAdapter(adapter);

                // Optionally set "Select" as the default selected item
                valveIdSpinner.setSelection(0);

                valveIdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Get the selected item
                        String selectedValveId = parent.getItemAtPosition(position).toString();
                        // Handle the selected Valve ID if needed
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case when no item is selected, if needed
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to retrieve data: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private List<String> parseValveIds(String jsonResponse) {
        List<String> valveIds = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valveObject = jsonArray.getJSONObject(i);
                String valveId = valveObject.getString("valveid");
                valveIds.add(valveId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return valveIds;
    }


}

