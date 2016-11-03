package com.forfun.wdh.passwordapp.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import com.forfun.wdh.passwordapp.R;
import com.forfun.wdh.passwordapp.service.PasswordService;

public class PersistOptionsActivity extends AppCompatActivity {

    PasswordService pws;
    CheckBox password;
    CheckBox saltFile;
    CheckBox charProf;
    CheckBox hashAlg;
    CheckBox rounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persist_options);
        pws = (PasswordService)getIntent().getSerializableExtra("pws");
        password = (CheckBox)findViewById(R.id.cbpassword);
        saltFile = (CheckBox)findViewById(R.id.cbsaltfile);
        charProf = (CheckBox)findViewById(R.id.cbcharprof);
        hashAlg = (CheckBox)findViewById(R.id.cbhashalg);
        rounds = (CheckBox)findViewById(R.id.cbrounds);
        password.setChecked(pws.isSavePassword());
        saltFile.setChecked(pws.isSaveSaltFileName());
        charProf.setChecked(pws.isSaveCharProfile());
        hashAlg.setChecked(pws.isSaveHashAlgorithm());
        rounds.setChecked(pws.isSaveRounds());
    }

    public void onClickChangePersistentData(View view) {
        Intent returnIntent = new Intent();
        pws.setSavePassword(password.isChecked());
        pws.setSaveSaltFile(saltFile.isChecked());
        pws.setSaveCharProfile(charProf.isChecked());
        pws.setSaveHashAlgorithm(hashAlg.isChecked());
        pws.setSaveRounds(rounds.isChecked());
        returnIntent.putExtra("pws", pws);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
