package com.resolvebug.app.bahikhata;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;
    private TextView fingerPrintMessage;
    private ImageView fingerPrintScanner;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) { // Too many attempts, try later
        this.update(errString.toString(),R.drawable.ic_exclamation);
    }

    @Override
    public void onAuthenticationFailed() {  // wrong finger
        this.update("Fingerprint not recognized",R.drawable.ic_exclamation);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {   // finger moved too fast
        this.update(helpString.toString(),R.drawable.ic_exclamation);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {     // success
        this.update((context).getString(R.string.EMPTY_STRING),R.drawable.ic_success);
        context.startActivity(new Intent(context, MainActivityOld.class));
        ((Activity)context).finish();
    }

    private void update(String message, int imageSrc) {
        fingerPrintMessage = ((Activity)context).findViewById(R.id.fingerPrintMessage);
        fingerPrintScanner = ((Activity)context).findViewById(R.id.fingerPrintScanner);
        fingerPrintMessage.setVisibility(View.VISIBLE);
        fingerPrintMessage.setText(message);
        fingerPrintScanner.setImageResource(imageSrc);
    }
}
