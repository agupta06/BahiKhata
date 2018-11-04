package com.resolvebug.app.bahikhata;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private TextView fingerPrintMessage;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an Auth Error" + errString);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("There was an Auth Failure");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("There was an Auth Help" + helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app");
        context.startActivity(new Intent(context, MainActivity.class));
        ((Activity)context).finish();
    }

    private void update(String message) {
        fingerPrintMessage = ((Activity)context).findViewById(R.id.fingerPrintMessage);
        fingerPrintMessage.setText(message);
    }
}
