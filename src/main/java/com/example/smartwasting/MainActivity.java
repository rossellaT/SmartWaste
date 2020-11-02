package com.example.smartwasting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;

public class MainActivity extends AppCompatActivity {

    TextView text;
    Button btn;
    CreateIotClient cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FindBinsActivity.class);
                startActivity(intent);
            }
        });
    }

}
