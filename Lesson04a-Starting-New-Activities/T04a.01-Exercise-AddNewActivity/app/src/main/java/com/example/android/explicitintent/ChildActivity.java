package com.example.android.explicitintent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ChildActivity extends AppCompatActivity {

    TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        displayText = findViewById(R.id.tv_display);

    }

}
