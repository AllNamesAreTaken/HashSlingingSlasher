package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.PasswordService;

import java.io.IOException;

public class OptionsViewActivity extends AppCompatActivity {
    private PasswordService pws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pws = (PasswordService)getIntent().getSerializableExtra("pws");
        setContentView(R.layout.activity_options_view);
    }

    public void onClickChangeCharactersButton(View view) {
        Intent i = new Intent(this, ChangeCharactersActivity.class);
        i.putExtra("pws", pws);
        startActivityForResult(i, 1);
    }

    public void onClickChangeRoundsButton(View view) {
        Intent i = new Intent(this, ChangeRoundsActivity.class);
        i.putExtra("pws", pws);
        startActivityForResult(i, 2);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("pws", pws);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                pws = (PasswordService)data.getSerializableExtra("pws");
            }
        }if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                pws = (PasswordService)data.getSerializableExtra("pws");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
