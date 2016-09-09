package com.forfun.wdh.passwordapp.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.MissingPasswordException;
import com.forfun.wdh.passwordapp.service.MissingSaltException;
import com.forfun.wdh.passwordapp.service.PasswordService;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

public class InputScreen extends AppCompatActivity {

    public static final int  MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 112;
    private GoogleApiClient client;
    private EditText passwordBox, passphraseBox, generatedTextBox, saltLocationBox, saltNameBox;
    private String passwordS = "", generatedTextS = "", saltLocationS = "", saltNameS = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        init();
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
        super.onDestroy();
        Log.i("Tag", "onDestroy");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        passwordS = new String(savedInstanceState.getCharArray("passwordS"));
        generatedTextS = new String(savedInstanceState.getCharArray("generatedTextS"));
        saltLocationS = new String(savedInstanceState.getCharArray("saltLocationS"));
        saltNameS = new String(savedInstanceState.getCharArray("saltNameS"));
        Log.i("Tag", "onRestoreInstanceState");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharArray("passwordS", passwordS.toCharArray());
        outState.putCharArray("generatedTextS", generatedTextS.toCharArray());
        outState.putCharArray("saltLocationS", saltLocationS.toCharArray());
        outState.putCharArray("saltNameS", saltNameS.toCharArray());
        super.onSaveInstanceState(outState);
        Log.i("Tag", "onSaveInstanceState");
    }

    private void init() {
        mainViewState();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {
                System.out.println("Uh well fix this.");
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "It, uh, it worked. Thanks :)", Toast.LENGTH_LONG).show();
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

    //View States
    private void mainViewState() {
        setContentView(R.layout.main_view);
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

    private void passwordChangeViewState() {
        setContentView(R.layout.password_select_view);
        passwordBox = (EditText)findViewById(R.id.passwordBox);
        passwordBox.setText(passwordS);
    }

    private void saltChangeViewState() {
        setContentView(R.layout.salt_select_view);
        saltLocationBox = (EditText)findViewById(R.id.editTextSaltLocation);
        saltLocationS = Environment.getExternalStorageDirectory().toString() + "/HashSlasher/salt/";
        saltLocationBox.setText(saltLocationS);
        saltNameBox = (EditText)findViewById(R.id.editTextSaltFile);
        saltNameS = "default.salt";
        saltNameBox.setText(saltNameS);
    }

    private void optionsViewState() {
        setContentView(R.layout.options_view);
    }
    //End View States

    //Main Screen
    public void onClickActivateButton(View v) {
        PasswordService pws = new PasswordService();
        pws.setPassword(passwordS);
        String generateFormat = "";
        String passphrase = passphraseBox.getText().toString();
        try {
            String gf = pws.generatePassblock(passphrase, 4, saltLocationS, saltNameS);
            for (int i = 0; i < gf.length(); i += 16) {
                for (int j = i; (j < (i + 15)) && (j < gf.length()); j++) {
                    generateFormat += gf.charAt(j);
                }
                if (i + 15 < gf.length()) {
                    generateFormat += gf.charAt(i + 15) + "\n";
                }
            }
            generatedTextS = generateFormat;
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
        }
    }

    public void onClickSelectSaltButton(View v) {
        saltChangeViewState();
    }

    public void onClickChangePasswordView(View v) {
        passwordChangeViewState();
    }
    //End Main Screen

    //Password Screen
    public void onClickChangePassword(View v) {
        passwordS = passwordBox.getText().toString();
        mainViewState();
    }

    public void onClickCancel(View v) {
        mainViewState();
    }
    //End Password Screen

    //Salt Screen
    public void onClickSelectSalt(View v) {
        saltLocationS = saltLocationBox.getText().toString();
        saltNameS = saltNameBox.getText().toString();
        mainViewState();
    }

    public void onClickCreateSaltButton(View v) {
        saltLocationS = saltLocationBox.getText().toString();
        saltNameS = saltNameBox.getText().toString();
        PasswordService pws = new PasswordService();
        String saltName = pws.generateSalt(saltLocationS, saltNameS);
        saltNameBox.setText(saltName);
    }
    //End Salt Screen

    //Options Screen
    public void onClickOptionsButton(View v) {
        optionsViewState();
    }

    public void onClickOptionsBackButton(View v) {
        mainViewState();
    }
    //End Options Screen
}
