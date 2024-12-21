package com.example.new_iwdms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Signup extends AppCompatActivity {
    private EditText editTextName, editTextUsername, editTextEmail, editTextPassword;
    private Button buttonSignUp;
    private TextView errorName, errorUsername, errorEmail, errorPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        errorName = findViewById(R.id.errorName);
        errorUsername = findViewById(R.id.errorUsername);
        errorEmail = findViewById(R.id.errorEmail);
        errorPassword = findViewById(R.id.errorPassword);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    Intent intent = new Intent(Signup.this, intropage.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        errorName.setVisibility(View.GONE);
        errorUsername.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);

        if (editTextName.getText().toString().trim().isEmpty()) {
            isValid = false;
            errorName.setText("Name is required");
            errorName.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextName);
        }

        if (editTextUsername.getText().toString().trim().isEmpty()) {
            isValid = false;
            errorUsername.setText("Username is required");
            errorUsername.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextUsername);
        }

        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            isValid = false;
            errorEmail.setText("Email is required");
            errorEmail.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextEmail);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false;
            errorEmail.setText("Invalid email format");
            errorEmail.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextEmail);
        }

        if (editTextPassword.getText().toString().trim().isEmpty()) {
            isValid = false;
            errorPassword.setText("Password is required");
            errorPassword.setVisibility(View.VISIBLE);
            startShakeAnimation(editTextPassword);
        }

        return isValid;
    }

    private void startShakeAnimation(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        view.startAnimation(shake);
    }}