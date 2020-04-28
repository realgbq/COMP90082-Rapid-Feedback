package com.example.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

// Activity for instructions page.

public class Activity_Help extends AppCompatActivity {
    private ExpandableListView helpList;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        init();
    }

    public void init() {
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(openHelpPage);
    }


    public View.OnClickListener openHelpPage = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Activity_Help.this, Activity_Homepage.class);
            startActivity(intent);
        }
    };
}
