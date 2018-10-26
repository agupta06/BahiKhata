package com.resolvebug.app.bahikhata;

import android.app.Application;

public class GlobalOperations extends Application {

    private boolean darkTheme;

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }
}
