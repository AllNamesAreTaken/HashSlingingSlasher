package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.williamdt_personal.passwordapp.R;
import com.forfun.wdh.passwordapp.service.PasswordService;

import java.io.File;

public class SaltSelectActivity extends AppCompatActivity {

    private EditText saltLocationBox, saltNameBox;
    private ListView fileListView;
    private String saltLocationS = "", saltNameS = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salt_select);
        PasswordService pws = new PasswordService();
        saltLocationS = getIntent().getStringExtra("saltLocationS");
        saltNameS = getIntent().getStringExtra("saltNameS");
        if(!pws.passwordDirExists(saltLocationS))
        {
            pws.createPasswordDirectory(saltLocationS);
        }
        saltLocationBox = (EditText)findViewById(R.id.editTextSaltLocation);
        saltLocationBox.setText(saltLocationS);
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
        saltLocationS = saltLocationBox.getText().toString();
        saltNameS = saltNameBox.getText().toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("saltLocationS", saltLocationS);
        returnIntent.putExtra("saltNameS", saltNameS);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickCreateSaltButton(View v) {
        saltLocationS = saltLocationBox.getText().toString();
        saltNameS = saltNameBox.getText().toString();
        PasswordService pws = new PasswordService();
        saltNameS = pws.generateSalt(saltLocationS, saltNameS);
        saltNameBox.setText(saltNameS);
        refreshFiles();
    }
    //End Salt Screen
}
