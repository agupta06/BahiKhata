package com.resolvebug.app.bahikhata;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageSupportFragment extends Fragment {

    public AdView adView;
    private TextView pageTitle;
    private RadioGroup radioGroup;

    public LanguageSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language_support, container, false);
        loadLocale();
        initialize(view);
        setAdView();
        setTitleFont();
        onRadioButtonClicked();
        return view;
    }

    private void initialize(View view) {
        adView = view.findViewById(R.id.adView);
        pageTitle = view.findViewById(R.id.pageTitle);
        radioGroup = view.findViewById(R.id.languages);
        initializeSharedPreferences();
    }

    private void initializeSharedPreferences() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String darkTheme = sharedPreferences.getString("darkTheme", "");
        if (darkTheme != null && darkTheme.equals("")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("darkTheme", "");
            editor.apply();
        }
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookie-Regular.ttf");
        pageTitle.setTypeface(typeface);
    }

    private void setAdView() {
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

    public void onRadioButtonClicked() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.englishLanguage:
                        setLocale("en");
                        recreate();
                        break;
                    case R.id.hindiLanguage:
                        setLocale("hi");
                        recreate();
                        break;
                    case R.id.marathiLanguage:
                        setLocale("mr");
                        recreate();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void recreate() {
        // Reload current fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_frame, LanguageSupportFragment.this).commit();
    }

    public void loadLocale() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("App_Language", "");
        setLocale(language);
    }

    private void setLocale(String selectedLangauge) {
        Locale locale = new Locale(selectedLangauge);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("App_Language", selectedLangauge);
        editor.apply();
    }
}
