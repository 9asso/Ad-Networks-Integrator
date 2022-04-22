package com.kscodes.ani;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class ANChooser {

    public static InterstitialAd mInterstitialAd;
    
    public static void ANIronSourceBanner(Context c){
        Toast.makeText(c, "ANIronSource", Toast.LENGTH_SHORT).show();
    }

    public static void ANApplovinMaxBanner(Context c){
        Toast.makeText(c, "ANApplovinMax", Toast.LENGTH_SHORT).show();
    }

    public static void ANAdmobBanner(Context c){
        Toast.makeText(c, "ANAdmob", Toast.LENGTH_SHORT).show();
    }

    public static void ANAdmobInterstitial(Activity activity, String ad_unit_admob_interstitial){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, ad_unit_admob_interstitial, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(activity);
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
}
