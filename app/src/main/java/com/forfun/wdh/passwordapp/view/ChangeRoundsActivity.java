package com.forfun.wdh.passwordapp.view;
import com.forfun.wdh.passwordapp.R;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.forfun.wdh.passwordapp.service.PasswordService;

public class ChangeRoundsActivity extends AppCompatActivity {


    private EditText changeRoundsBox;
    private int changeRoundsI;
    private PasswordService pws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_rounds);
        pws = (PasswordService)getIntent().getSerializableExtra("pws");
        changeRoundsI = pws.getRounds();
        changeRoundsBox = (EditText)findViewById(R.id.editTextNum);
        changeRoundsBox.setText(changeRoundsI + "");
    }

    public void onClickChangeRounds(View view) {
        Intent returnIntent = new Intent();
        changeRoundsI = Integer.parseInt(changeRoundsBox.getText().toString());
        pws.setRounds(changeRoundsI);
        returnIntent.putExtra("pws", pws);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void onClickResetToDefaultRounds(View view) {
        changeRoundsI = 4;
        pws.setRounds(changeRoundsI);
        changeRoundsBox.setText(changeRoundsI + "");
    }
}
