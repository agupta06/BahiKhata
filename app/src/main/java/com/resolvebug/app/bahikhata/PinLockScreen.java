package com.resolvebug.app.bahikhata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;
import android.widget.Toast;

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

    private EditText pinText1;
    private EditText pinText2;
    private EditText pinText3;
    private EditText pinText4;
    private Button validatePin;

    private String inputPin;
    private String TEMP_PIN = "1234";

    // Finger Print Authentication
    private TextView fingerPrintMessage;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_lock_screen);

        pinText1 = findViewById(R.id.pinText1);
        pinText2 = findViewById(R.id.pinText2);
        pinText3 = findViewById(R.id.pinText3);
        pinText4 = findViewById(R.id.pinText4);

        pinText1.addTextChangedListener(textWatcher);
        pinText2.addTextChangedListener(textWatcher);
        pinText3.addTextChangedListener(textWatcher);
        pinText4.addTextChangedListener(textWatcher);

        validatePin = findViewById(R.id.validatePin);
        validatePin.setEnabled(false);
        validatePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputPin.equals(TEMP_PIN)) {
                    startMainActivity();
                } else {
                    Toast.makeText(PinLockScreen.this, "Incorrect Pin.", Toast.LENGTH_LONG).show();
                }
            }
        });


        // Finger Print Authentication
        // TODO Check 1 : Android version should be greater than or equal to Marshmellow
        // TODO Check 2 : Device has fingerprint scanner
        // TODO Check 3 : Have permission to use fingerprint scanner in the app
        // TODO Check 4 : Lock screen is secured with atleast 1 type of lock - pin/pattern
        // TODO Check 5 : Atleast 1 fingerprint is registered

        fingerPrintMessage = findViewById(R.id.fingerPrintMessage);

        // CHECK 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) { // CHECK 2
                // TODO :  show some alternate login method
                fingerPrintMessage.setText("Finger Print Scanner Not Detected");
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {   // CHECK 3
                fingerPrintMessage.setText("Permission not granted top use Finger print scanner");
            } else if (!keyguardManager.isKeyguardSecure()) {   // CHECK 4
                fingerPrintMessage.setText("Add lock to your phone in settings");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {    // CHECK 5
                fingerPrintMessage.setText("Please add atleast 1 fingerprint to use this feature in settings");
            } else {
                fingerPrintMessage.setText("Place your finger on scanner to proceed");
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
                validatePin.setEnabled(true);
                inputPin = pin1 + pin2 + pin3 + pin4;
            } else {
                validatePin.setEnabled(false);
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
}
