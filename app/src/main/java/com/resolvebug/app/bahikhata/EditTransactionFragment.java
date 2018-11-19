package com.resolvebug.app.bahikhata;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class EditTransactionFragment extends Fragment {

    // TextView
    private TextView pageTitle;

    // ImageView
    private ImageView closeFragment;

    // AdView
    private AdView adView;

    public EditTransactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_transaction, container, false);

        initializeId(view);
        setTitleFont();
        setAdView(view);
        updateTransaction();
        closeEditTransactionFragment();

        return view;
    }

    private void initializeId(View view) {
        pageTitle = view.findViewById(R.id.pageTitle);
        closeFragment = view.findViewById(R.id.closeFragment);
        adView = view.findViewById(R.id.adView);
    }

    private void setTitleFont() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Cookie-Regular.ttf");
        pageTitle.setTypeface(typeface);
    }

    private void setAdView(View view) {
        final AdView adView = view.findViewById(R.id.adView);
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

    private void closeEditTransactionFragment() {
        closeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAllTransactionFragment();
            }
        });
    }

    private void openAllTransactionFragment() {
        Intent intent = new Intent(getActivity(), TrialMainActivity.class);
        getActivity().startActivity(intent);
    }

    private void updateTransaction(){
        Bundle bundle = new Bundle();
        String notesTxId = bundle.getString("notesTxId");
        String notesTxAmount = bundle.getString("notesTxAmount");
        String notesTxMessage = bundle.getString("notesTxMessage");
        String notesTxType = bundle.getString("notesTxType");
        String notesTxImportant = bundle.getString("notesTxImportant");
        String notesTxDate = bundle.getString("notesTxDate");
        String notesTxTime = bundle.getString("notesTxTime");
    }
}
