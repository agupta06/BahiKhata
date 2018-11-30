package com.resolvebug.app.bahikhata;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageSupportFragment extends Fragment {

    public AdView adView;
    private TextView pageTitle;

    public LanguageSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_language_support, container, false);
        initialize(view);
        setAdView();
        setTitleFont();
        return view;
    }

    private void initialize(View view) {
        adView = view.findViewById(R.id.adView);
        pageTitle = view.findViewById(R.id.pageTitle);
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
}
