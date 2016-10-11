package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.PasswordService;

import java.io.File;
import java.io.IOException;

public class SaltSelectActivity extends AppCompatActivity {

    private EditText saltLocationBox, saltNameBox;
    private ListView fileListView;
    private String saltLocationS = "", saltNameS = "";
    PasswordService pws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salt_select);
        pws = (PasswordService)getIntent().getSerializableExtra("pws");
        saltLocationS = pws.getBaseDirectory() + "/HashSlingingSlasher/salt/";
        saltLocationBox = (EditText)findViewById(R.id.editTextSaltLocation);
        saltLocationBox.setText(saltLocationS);
        saltNameS = pws.getSaltFileName();
        saltNameBox = (EditText)findViewById(R.id.editTextSaltFile);
        saltNameBox.setText(saltNameS);
        fileListView = (ListView) findViewById(R.id.FileListView);
        refreshFiles();
    }

    public void refreshFiles(){
        File dir = new File(saltLocationS);
        String[] files = dir.list();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, files);
        fileListView.setAdapter(adapter);
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) fileListView.getItemAtPosition(position);
                saltNameS = itemValue;
                saltNameBox.setText(itemValue);
            }
        });
    }

    //Salt Screen
    public void onClickSelectSalt(View v) {
//        saltLocationS = saltLocationBox.getText().toString();
        saltNameS = saltNameBox.getText().toString();
        pws.setSaltFileName(saltNameS);
        try {
            pws.loadSaltFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("pws", pws);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickCreateSaltButton(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString();
                pws.setSaltFileName(newName);
                try {
                    //Handles setting saltName
                    pws.createNewSaltFile(newName);
                    //Must be loaded
                    pws.loadSaltFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saltNameS = pws.getSaltFileName();
                saltNameBox.setText(saltNameS);
                refreshFiles();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    //End Salt Screen
}
