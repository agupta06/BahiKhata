package com.resolvebug.app.bahikhata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Themes extends AppCompatActivity {

    private Switch mySwitch;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final GlobalOperations globalOperations = (GlobalOperations) getApplication();
        setThemeModeToActivity(globalOperations);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
        mySwitch = findViewById(R.id.mySwitch);
        setSwitchStatus(globalOperations);
        setSwitchOperation(globalOperations);
        setButtonClickListener();
    }

    private void setSwitchOperation(final GlobalOperations globalOperations) {
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    globalOperations.setDarkTheme(true);
                    restartApp();
                } else {
                    globalOperations.setDarkTheme(false);
                    restartApp();
                }
            }
        });
    }

    private void setButtonClickListener() {
        button = findViewById(R.id.themeTwoButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Themes.this, ThemeTwo.class);
                startActivity(intent);
            }
        });
    }

    private void setThemeModeToActivity(GlobalOperations globalOperations) {
        if (globalOperations.isDarkTheme()) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void setSwitchStatus(GlobalOperations globalOperations) {
        if (globalOperations.isDarkTheme()) {
            mySwitch.setChecked(true);
            globalOperations.setDarkTheme(true);
        } else {
            mySwitch.setChecked(false);
            globalOperations.setDarkTheme(false);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), Themes.class);
        startActivity(intent);
        finish();
    }
}