package com.example.jiongyi.foodemblem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.example.jiongyi.foodemblem.service.FoodEmblemService;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


/**
 * Created by JiongYi on 5/3/2018.
 */

public class StartupPageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        Intent intent = getIntent();
        setContentView(R.layout.activity_startuppage);
        Intent service = new Intent(this, FoodEmblemService.class);
        startService(service);
        SharedPreferences sp = getSharedPreferences("FoodEmblem",MODE_PRIVATE);
        if (sp.getBoolean("isloggedin",false) == true){
            Intent i = new Intent(this,HomeActivity.class);
            startActivity(i);
        }
        Toolbar myToolbar = (Toolbar) findViewById(R.id.emblemtoolbar);
        setSupportActionBar(myToolbar);
        Button button = (Button) findViewById(R.id.signin);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent i = new Intent(StartupPageActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
