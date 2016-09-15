package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.williamdt_personal.passwordapp.R;

public class PasswordSelectActivity extends AppCompatActivity {

    private EditText passwordBox;
    private String passwordS = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_select);
        passwordS = getIntent().getStringExtra("passwordS");
        passwordBox = (EditText)findViewById(R.id.passwordBox);
        passwordBox.setText(passwordS);
    }

    //Password Screen
    public void onClickChangePassword(View v) {
        passwordS = passwordBox.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("passwordS", passwordS);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
    //End Password Screen
}
