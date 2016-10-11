package com.forfun.wdh.passwordapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.exceptions.MissingCharProfileException;
import com.forfun.wdh.passwordapp.exceptions.MissingDirectoryException;
import com.forfun.wdh.passwordapp.exceptions.MissingPasswordException;
import com.forfun.wdh.passwordapp.exceptions.MissingSaltException;
import com.forfun.wdh.passwordapp.service.PasswordService;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class InputScreenActivity extends AppCompatActivity {

    public static final int  MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 112;
    private GoogleApiClient client;
    private EditText passphraseBox, generatedTextBox;
    public PasswordService pws = new PasswordService(Environment.getExternalStorageDirectory().toString());
    private String generatedTextS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mainViewState();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                System.out.println("Permission to write files not granted.");
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        try {
            pws.createMissingHSSDirectories();
            pws.loadSettings();
            pws.loadCache();
            pws.loadSaltFile();
        }
        catch(Exception e) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Tag", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Tag", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Tag", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Tag", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("Tag", "onRestart");
    }

    @Override
    protected void onDestroy() {
        try {
            pws.saveCache();
            pws.saveSettings();
        }
        catch(Exception e) {

        }
        super.onDestroy();
        Log.i("Tag", "onDestroy");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pws = (PasswordService) savedInstanceState.getSerializable("pws");
        Log.i("Tag", "onRestoreInstanceState");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("pws", pws);
        super.onSaveInstanceState(outState);
        Log.i("Tag", "onSaveInstanceState");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "Access to write to storage granted :)", Toast.LENGTH_LONG).show();
                    try {
                        pws.createMissingHSSDirectories();
                    } catch (MissingDirectoryException e) {
                        e.printStackTrace();
                    }
                } else
                {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("Tag", "Hi");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                pws.setPassword(data.getStringExtra("passwordS"));
            }
        }
        if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                pws = (PasswordService)data.getSerializableExtra("pws");
                try {
                    pws.loadSaltFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == 3) {
            pws = (PasswordService) data.getSerializableExtra("pws");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //View States
    private void mainViewState() {
        setContentView(R.layout.activity_input_screen);
        passphraseBox = (EditText)findViewById(R.id.passphraseBox);
        generatedTextBox = (EditText)findViewById(R.id.generatedText);
        String gt = "****************";
        for(int i = 0; i < 15; i++)
        {
            gt += "\n";
            for(int j = 0; j < 16; j++)
            {
                gt += "*";
            }
        }
        generatedTextS = gt;
        generatedTextBox.setText(generatedTextS);
    }

    //Main Screen
    public void onClickActivateButton(View v) {
        StringBuilder generateFormat = new StringBuilder();
        String passphrase = passphraseBox.getText().toString();
        try {
            String gf = pws.generatePassblock(passphrase);
            for (int i = 0; i < gf.length(); i += 16) {
                for (int j = i; (j < (i + 15)) && (j < gf.length()); j++) {
                    generateFormat.append(gf.charAt(j));
                }
                if (i + 15 < gf.length()) {
                    generateFormat.append(gf.charAt(i + 15) + "\n");
                }
            }
            generatedTextS = generateFormat.toString();
            generatedTextBox.setText(generatedTextS);
            View view = this.getCurrentFocus();
            if(view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        catch (MissingPasswordException e) {
            generatedTextS = "Insert password";
            generatedTextBox.setText(generatedTextS);
        } catch (MissingSaltException e) {
            generatedTextS = "Select salt";
            generatedTextBox.setText(generatedTextS);
        } catch (NoSuchAlgorithmException e) {
            generatedTextS = "Algorithm " + pws.getHashAlgorithm() + " is not supported (or doesn't exist)";
            generatedTextBox.setText(generatedTextS);
        } catch (MissingCharProfileException e) {
            generatedTextS = "Missing charProfile";
            generatedTextBox.setText(generatedTextS);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickOptionsButton(View v) {
        Intent i = new Intent(this, OptionsViewActivity.class);
        i.putExtra("pws", pws);
        startActivityForResult(i, 3);
    }

    public void onClickSelectSaltButton(View v) {
        Intent i = new Intent(this, SaltSelectActivity.class);
        i.putExtra("pws", pws);
        startActivityForResult(i, 2);
    }

    public void onClickChangePasswordView(View v) {
        Intent i = new Intent(this, PasswordSelectActivity.class);
        i.putExtra("passwordS", pws.getPassword());
        startActivityForResult(i, 1);
    }
}
