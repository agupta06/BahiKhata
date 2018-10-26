package com.resolvebug.app.bahikhata;

import android.app.Application;

public class GlobalOperations extends Application {

    private boolean darkTheme;
    private boolean phoneLogin;

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public boolean isPhoneLogin() {
        return phoneLogin;
    }

    public void setPhoneLogin(boolean phoneLogin) {
        this.phoneLogin = phoneLogin;
    }
}
