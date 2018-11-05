package com.resolvebug.app.bahikhata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class PinLockScreen extends AppCompatActivity {

    private AdView adView;

    private EditText pinText1;
    private EditText pinText2;
    private EditText pinText3;
    private EditText pinText4;
    private ImageButton validatePin;
    private TextView invalidPinText;
    private TextView fingerPrintMessage;

    private String inputPin;
    private String TEMP_PIN = "1234";

    // Finger Print Authentication
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";
    private ImageView fingerPrintScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock_screen);
        setAppName();
        setAdsView();

        pinText1 = findViewById(R.id.pinText1);
        pinText2 = findViewById(R.id.pinText2);
        pinText3 = findViewById(R.id.pinText3);
        pinText4 = findViewById(R.id.pinText4);

        pinText1.addTextChangedListener(textWatcher);
        pinText2.addTextChangedListener(textWatcher);
        pinText3.addTextChangedListener(textWatcher);
        pinText4.addTextChangedListener(textWatcher);

        invalidPinText = findViewById(R.id.invalidPinText);
        fingerPrintMessage = findViewById(R.id.fingerPrintMessage);

        validatePin = findViewById(R.id.validatePin);
        //validatePin.setEnabled(false);
        validatePin.setVisibility(View.INVISIBLE);
        invalidPinText.setVisibility(View.INVISIBLE);
        fingerPrintMessage.setVisibility(View.INVISIBLE);
        validatePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputPin.equals(TEMP_PIN)) {
                    startMainActivity();
                } else {
                    invalidPinText.setVisibility(View.VISIBLE);
                    invalidPinText.setText("Wrong PIN");
                    invalidPinText.setTextColor(getResources().getColor(R.color.colorLogo));
                }
            }
        });


        // Finger Print Authentication
        // TODO Check 1 : Android version should be greater than or equal to Marshmallow
        // TODO Check 2 : Device has fingerprint scanner
        // TODO Check 3 : Have permission to use fingerprint scanner in the app
        // TODO Check 4 : Lock screen is secured with atleast 1 type of lock - pin/pattern
        // TODO Check 5 : Atleast 1 fingerprint is registered

        fingerPrintScanner = findViewById(R.id.fingerPrintScanner);

        // CHECK 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) { // CHECK 2
                // TODO :  show some alternate login method
                fingerPrintScanner.setImageResource(android.R.color.transparent);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {   // CHECK 3
                fingerPrintScanner.setImageResource(android.R.color.transparent);
            } else if (!keyguardManager.isKeyguardSecure()) {   // CHECK 4
                fingerPrintScanner.setImageResource(android.R.color.transparent);
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {    // CHECK 5
                fingerPrintScanner.setImageResource(android.R.color.transparent);
            } else {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                }
            }

        }

    }

    private void startMainActivity() {
        startActivity(new Intent(PinLockScreen.this, MainActivity.class));
        finish();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String pin1 = pinText1.getText().toString().trim();
            String pin2 = pinText2.getText().toString().trim();
            String pin3 = pinText3.getText().toString().trim();
            String pin4 = pinText4.getText().toString().trim();
            if (pin1.length() == 1) {
                pinText2.requestFocus();
            }
            if (pin2.length() == 1) {
                pinText3.requestFocus();
            }
            if (pin3.length() == 1) {
                pinText4.requestFocus();
            }


            if (!pin1.isEmpty() && !pin2.isEmpty() && !pin3.isEmpty() && !pin4.isEmpty()) {
                //validatePin.setEnabled(true);
                validatePin.setVisibility(View.VISIBLE);
                inputPin = pin1 + pin2 + pin3 + pin4;
            } else {
                validatePin.setVisibility(View.INVISIBLE);
                invalidPinText.setText(getString(R.string.EMPTY_STRING));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void setAppName() {
        TextView appName;
        appName = findViewById(R.id.main_toolbar_title);
        Typeface typeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_app_name));
        appName.setTypeface(typeface);
    }

    private void setAdsView() {
        adView = findViewById(R.id.loginAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                adView.setVisibility(View.GONE);
            }
        });
    }
}
