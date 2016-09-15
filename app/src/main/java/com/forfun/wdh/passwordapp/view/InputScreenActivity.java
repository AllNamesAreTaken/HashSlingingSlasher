package com.forfun.wdh.passwordapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.MissingPasswordException;
import com.forfun.wdh.passwordapp.service.MissingSaltException;
import com.forfun.wdh.passwordapp.service.PasswordService;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class InputScreenActivity extends AppCompatActivity {

    public static final int  MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 112;
    private GoogleApiClient client;
    private EditText passwordBox, passphraseBox, generatedTextBox, saltLocationBox, saltNameBox;
    private ListView fileListView;
    private String passwordS = "", generatedTextS = "", saltLocationS = "", saltNameS = "";
    private String settingsLoc = Environment.getExternalStorageDirectory().toString() + "/HashSlasher/settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        readSettings();
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
        writeSettings();
        super.onDestroy();
        Log.i("Tag", "onDestroy");
    }

    private void writeSettings() {
        File settings = new File(settingsLoc);
        settings.mkdirs();
        settings = new File(settingsLoc + "/settings.dat");
        try {
            if(!settings.exists())
            {
                settings.createNewFile();
            }
            BufferedWriter writeStream = new BufferedWriter(new FileWriter(settingsLoc + "/settings.dat", false));
            writeStream.write(passwordS);
            writeStream.newLine();
            writeStream.write(saltNameS);
            writeStream.newLine();
            writeStream.write(saltLocationS);
            writeStream.newLine();
            writeStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSettings() {
        try {
            File settings = new File(settingsLoc);
            boolean hasDir = settings.mkdirs();
            settings = new File(settingsLoc + "/settings.dat");
            boolean hasFile = settings.createNewFile();
            boolean dirExists = settings.exists();
            boolean isDir = settings.isDirectory();
            boolean isFile = settings.isFile();
            if(hasDir && hasFile){
                System.out.print("What");
            }
            BufferedReader readStream = new BufferedReader(new FileReader(settingsLoc + "/settings.dat"));
            passwordS = readStream.readLine();
            if(passwordS == null) {
                passwordS = "";
            }
            saltNameS = readStream.readLine();
            if(saltNameS == null) {
                saltNameS = "default.salt";
            }
            saltLocationS = readStream.readLine();
            if(saltLocationS == null) {
                saltLocationS = Environment.getExternalStorageDirectory().toString() + "/HashSlasher/salt/";
            }
            readStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this, "It, uh, it worked. Thanks :)", Toast.LENGTH_LONG).show();
                    PasswordService pws = new PasswordService();
                    if(!pws.passwordDirExists(Environment.getExternalStorageDirectory().toString()))
                    {
                        pws.createPasswordDirectory(Environment.getExternalStorageDirectory().toString());
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
                passwordS = data.getStringExtra("passwordS");
            }
        }
        if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                saltLocationS = data.getStringExtra("saltLocationS");
                saltNameS = data.getStringExtra("saltNameS");
            }
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

    public void onClickOptionsButton(View v) {
        startActivity(new Intent(InputScreenActivity.this, OptionsViewActivity.class));
    }

    public void onClickSelectSaltButton(View v) {
        Intent i = new Intent(this, SaltSelectActivity.class);
        i.putExtra("saltNameS", saltNameS);
        i.putExtra("saltLocationS", saltLocationS);
        startActivityForResult(i, 2);
    }

    public void onClickChangePasswordView(View v) {
        Intent i = new Intent(this, PasswordSelectActivity.class);
        i.putExtra("passwordS", passwordS);
        startActivityForResult(i, 1);
    }
}
