package com.resolvebug.app.bahikhata;

import android.app.Application;

public class GlobalOperations extends Application {

    private boolean darkTheme;
    private boolean phoneLogin;
    private String txToDate;
    private String txFromDate;

    public String getTxToDate() {
        return txToDate;
    }

    public void setTxToDate(String txToDate) {
        this.txToDate = txToDate;
    }

    public String getTxFromDate() {
        return txFromDate;
    }

    public void setTxFromDate(String txFromDate) {
        this.txFromDate = txFromDate;
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
