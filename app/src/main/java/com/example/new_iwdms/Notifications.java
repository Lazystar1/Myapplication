package com.example.new_iwdms;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends Fragment {
    private static final String API_URL = "https://demo.seminalsoftwarepvt.in/IWDMSMiddleware/API/NotificationDetails/";

    private Spinner alertSpinner;
    private EditText edCompFeedback;
    private RadioGroup radioGroup;
    private Button btnSubmit;

    public Notifications() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notifications");
        }

        if (!isNetworkConnected()) {
            showNoInternetDialog();
            return inflater.inflate(R.layout.fragment_notifications, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        initializeUI(view);
        loadAlerts();

        btnSubmit.setOnClickListener(v -> {
            try {
                if (isValidForm()) {
                    insertData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        return view;
    }
    private boolean isValidForm() {

        String selectedAlert = alertSpinner.getSelectedItem().toString();
        if (selectedAlert.equals("Select")) {
            showValidationDialog("Please select an alert.");
            return false;
        }
        // Check if a radio button is selected
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            showValidationDialog("Please select a priority.");
            return false;
        }

        // Check if an alert is selected


        // If all validations pass
        return true;
    }

    private void showValidationDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Validation Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void initializeUI(View view) {
        btnSubmit = view.findViewById(R.id.btnsubmit);
        radioGroup = view.findViewById(R.id.radioGroup);
        edCompFeedback = view.findViewById(R.id.edittext_complaints);
        alertSpinner = view.findViewById(R.id.spnAlerts);

        ArrayAdapter<CharSequence> adapterCodes = ArrayAdapter.createFromResource(getContext(),
                R.array.spinner_alerts, android.R.layout.simple_spinner_item);
        adapterCodes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alertSpinner.setAdapter(adapterCodes);
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
                .setPositiveButton("OK", (dialog, which) -> requireActivity().finish())
                .show();
    }

    private void loadAlerts() {
        API_TEMP apiTemp = new API_TEMP();

        apiTemp.executeGetRequest(apiTemp.baseUrl, apiTemp.GetNotALerts, new API_TEMP.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                List<String> alertsList = parseAlerts(response);
                alertsList.add(0, "Select");

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, alertsList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                alertSpinner.setAdapter(adapter);
                alertSpinner.setSelection(0);

                alertSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Handle selection if needed
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Handle case when no item is selected, if needed
                    }
                });
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Failed to retrieve data: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<String> parseAlerts(String jsonResponse) {
        List<String> alertsList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject alertObject = jsonArray.getJSONObject(i);
                String alert = alertObject.getString("alerts");
                alertsList.add(alert);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return alertsList;
    }

    private void insertData() {
        API_TEMP apiTemp = new API_TEMP();
        String selectedAlert = alertSpinner.getSelectedItem().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
            String selectedPriority = selectedRadioButton.getText().toString();
            String compFeedback = edCompFeedback.getText().toString();

            SharedPreferences sharedPref = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
            String latitude = sharedPref.getString("Latitude", "default_latitude");
            String longitude = sharedPref.getString("Longitude", "default_longitude");

            String urlParams = "Alerts=" + selectedAlert +
                    "&priority=" + selectedPriority +
                    "&CompFeedback=" + compFeedback +
                    "&Latitude=" + latitude +
                    "&Longitude=" + longitude;

            apiTemp.executePostRequest(apiTemp.baseUrl, apiTemp.postnotification, urlParams, new API_TEMP.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getContext(), "Inserted Successfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getContext(), "Failed To Insert", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
