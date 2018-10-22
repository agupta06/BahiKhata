package com.resolvebugs.app.bahikhata;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class ThemeTwo extends AppCompatActivity {

    private Switch mySwitch2;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final GlobalOperations globalOperations = (GlobalOperations) getApplication();
        setThemeModeToActivity(globalOperations);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_two);
        setButtonClickListener();
        mySwitch2 = findViewById(R.id.mySwitch2);
        setSwitchStatus(globalOperations);
        setSwitchOperation(globalOperations);
    }

    private void setSwitchOperation(final GlobalOperations globalOperations) {
        mySwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        button = findViewById(R.id.themeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThemeTwo.this, Themes.class);
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
            mySwitch2.setChecked(true);
        } else {
            mySwitch2.setChecked(false);
        }
    }

    private void restartApp() {
        Intent intent = new Intent(getApplicationContext(), ThemeTwo.class);
        startActivity(intent);
        finish();
    }
}