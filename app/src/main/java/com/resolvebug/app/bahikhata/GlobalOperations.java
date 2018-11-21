package com.resolvebug.app.bahikhata;

import android.app.Application;

public class GlobalOperations extends Application {

    private boolean darkTheme;
    private boolean phoneLogin;
    private long txToDate;

    public long getTxToDate() {
        return txToDate;
    }

    public void setTxToDate(long txToDate) {
        this.txToDate = txToDate;
    }

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
