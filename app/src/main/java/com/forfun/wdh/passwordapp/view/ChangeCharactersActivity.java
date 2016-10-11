package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.PasswordService;

public class ChangeCharactersActivity extends AppCompatActivity {

    private EditText changeCharBox;
    private String changeCarS = "";
    private PasswordService pws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_characters);
        pws = (PasswordService)getIntent().getSerializableExtra("pws");
        changeCarS = pws.getCharProfile();
        changeCharBox = (EditText)findViewById(R.id.editTextCCA);
        changeCharBox.setText(changeCarS);
    }

    public void onClickChangeCharProfile(View view) {
        Intent returnIntent = new Intent();
        changeCarS = changeCharBox.getText().toString();
        try {
            pws.setCharProfile(changeCarS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        returnIntent.putExtra("pws", pws);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickResetToDefault(View view) {
        changeCarS = pws.defaultCharProfile;
        changeCharBox.setText(changeCarS);
        try {
            pws.setCharProfile(changeCarS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
