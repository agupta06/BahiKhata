package com.resolvebug.app.bahikhata;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AllTransactionFragment extends Fragment {

    // TextView
    private TextView pageTitle;

    // Floating Action Button
    private FloatingActionButton addTransactionButton;

    // ViewPager
    private ViewPager viewPager;

    // AdView
    private AdView adView;

    // TabLayout
    private TabLayout tabLayout;

    public AllTransactionFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_transaction, container, false);

        initializeId(view);
        setTitleFont();
        setAdView(view);
        openCreateTransactionFragment();
        setupViewPager(viewPager);

        return view;
    }

    private void initializeId(View view) {
        pageTitle = view.findViewById(R.id.pageTitle);
        addTransactionButton = view.findViewById(R.id.addTransactionButton);
        viewPager = view.findViewById(R.id.viewPager);
        adView = view.findViewById(R.id.adView);
        tabLayout = view.findViewById(R.id.tabLayout);
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

    private void openCreateTransactionFragment() {
        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTransaction();
            }
        });
    }

    private void createTransaction() {
        CreateTransactionFragment createTransactionFragment = new CreateTransactionFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.transactionFrames, createTransactionFragment, "CreateTransactionFragment").commit();
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentAdapter adapter = new FragmentAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new NotesFragment(), "NOTES");
        adapter.addFragment(new CreditsFragment(), "CREDITS");
        adapter.addFragment(new DebitsFragment(), "DEBITS");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
