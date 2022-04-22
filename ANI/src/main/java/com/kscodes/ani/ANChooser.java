package com.kscodes.ani;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.applovin.mediation.ads.MaxInterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ANChooser {

    private static InterstitialAd mInterstitialAd;

    private static MaxInterstitialAd interstitialAd;

    public static void AdInit(Activity activity, String json_path){
        RequestQueue requestQueue_ = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest_ = new JsonObjectRequest(Request.Method.GET, json_path, null,
                response -> {
                    try{
                        JSONArray array = response.getJSONArray("data");
                        JSONObject jsonObject = array.getJSONObject(0);
                        String network = jsonObject.getString("network");

                        switch (network) {
                            case "admob":
                                MobileAds.initialize(activity, initializationStatus -> { });
                                break;
                            // case "is":
                            //     IronSource.init(activity, jsonObject.getString("iron_source_key"), IronSource.AD_UNIT.BANNER, IronSource.AD_UNIT.INTERSTITIAL);
                            //     break;
                            case "max":
                                AppLovinSdk.getInstance(activity).setMediationProvider("max");
                                AppLovinSdk.initializeSdk(activity, config -> { });
                                break;
                            // case "appodeal":
                            //     Appodeal.initialize(activity, jsonObject.getString("appodeal_app_key"),
                            //             Appodeal.BANNER_VIEW|Appodeal.INTERSTITIAL, true);
                            //     break;
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(activity, "json file not valid ! " + e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> { }
        );
        requestQueue_.add(jsonObjectRequest_);
    }

    public static void ShowBanner(Activity activity, String json_path, LinearLayout ad_frame) {
        RequestQueue requestQueue_ = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest_ = new JsonObjectRequest(Request.Method.GET, json_path, null,
                response -> {
                    try{
                        JSONArray array = response.getJSONArray("data");
                        JSONObject jsonObject = array.getJSONObject(0);
                        String network = jsonObject.getString("network");

                        switch (network) {
                            case "admob":
                                AdmobBanner(activity, ad_frame, jsonObject.getString("ad_unit_admob_banner"));
                                break;
                            // case "is":
                            //     IronSourceBanner(activity, ad_frame);
                            //     break;
                            case "max":
                                ApplovinMaxBanner(activity, jsonObject.getString("ad_unit_max_banner"), ad_frame);
                                break;
                            // case "appodeal":
                            //     AppodealBanner(activity, ad_frame, jsonObject.getString("appodeal_app_key"));
                            //     break;
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(activity, "json file not valid ! " + e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> { }
        );
        requestQueue_.add(jsonObjectRequest_);
    }

    public static void ShowInterstitial(Activity activity, String json_path, boolean notifier) {
        if (notifier){
            AdProgress.startAdProgress(activity);
        }
        RequestQueue requestQueue_ = Volley.newRequestQueue(activity);
        JsonObjectRequest jsonObjectRequest_ = new JsonObjectRequest(Request.Method.GET, json_path, null,
                response -> {
                    try{
                        JSONArray array = response.getJSONArray("data");
                        JSONObject jsonObject = array.getJSONObject(0);
                        String network = jsonObject.getString("network");

                        switch (network) {
                            case "admob":
                                AdmobInterstitial(activity, jsonObject.getString("ad_unit_admob_interstitial"));
                                break;
                            // case "is":
                            //     IronSourceInterstitial();
                            //     break;
                            case "max":
                                ApplovinMaxInterstitial(activity, jsonObject.getString("ad_unit_max_interstitial"));
                                break;
                            // case "appodeal":
                            //     AppodealInterstitial(activity, jsonObject.getString("appodeal_app_key"));
                            //     break;
                        }
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(activity, "json file not valid ! " + e, Toast.LENGTH_SHORT).show();
                    }
                },
                error -> { }
        );
        requestQueue_.add(jsonObjectRequest_);
    }

    private static void ApplovinMaxBanner(Activity activity, String ad_unit_max_banner_, LinearLayout ad_frame_){
        MaxAdView adView = new MaxAdView(ad_unit_max_banner_, activity);
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) { }
            @Override
            public void onAdCollapsed(MaxAd ad) { }
            @Override
            public void onAdLoaded(MaxAd ad) {
                ad_frame_.removeAllViews();
                ad_frame_.addView(adView);
            }
            @Override
            public void onAdDisplayed(MaxAd ad) { }
            @Override
            public void onAdHidden(MaxAd ad) { }
            @Override
            public void onAdClicked(MaxAd ad) { }
            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) { }
            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) { }
        });

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Get the adaptive banner height.
        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize( activity ).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx( activity, heightDp );

        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );
        adView.setGravity(Gravity.CENTER);
        adView.setExtraParameter( "adaptive_banner", "true" );
        adView.setBackgroundColor(Color.parseColor("#ffffff"));
        adView.stopAutoRefresh();
        adView.loadAd();
    }

    private static void ApplovinMaxInterstitial(Activity activity, String ad_unit_max_interstitial_){
        interstitialAd = new MaxInterstitialAd( ad_unit_max_interstitial_, activity );
        interstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                interstitialAd.showAd();
                if (AdProgress.dialog!=null)
                    AdProgress.dismissAdProgress();
            }
            @Override
            public void onAdDisplayed(MaxAd ad) { }
            @Override
            public void onAdHidden(MaxAd ad) {
                interstitialAd.loadAd();
            }
            @Override
            public void onAdClicked(MaxAd ad) { }
            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                interstitialAd.loadAd();
                if (AdProgress.dialog!=null)
                    AdProgress.dismissAdProgress();
            }
            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                if (AdProgress.dialog!=null)
                    AdProgress.dismissAdProgress();
            }
        });
        interstitialAd.loadAd();
    }

//     private static void AppodealInterstitial(Activity activity, String appodeal_app_key_) {
//         if (Appodeal.isInitialized(Appodeal.INTERSTITIAL)){
//             Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
//                 @Override
//                 public void onInterstitialLoaded(boolean isPrecache) {
//                     // Called when interstitial is loaded
//                     Appodeal.show(activity, Appodeal.INTERSTITIAL);
//                     if (AdProgress.dialog!=null)
//                         AdProgress.dismissAdProgress();
//                 }
//                 @Override
//                 public void onInterstitialFailedToLoad() {
//                     // Called when interstitial failed to load
//                     if (AdProgress.dialog!=null)
//                         AdProgress.dismissAdProgress();
//                 }
//                 @Override
//                 public void onInterstitialShown() {
//                     // Called when interstitial is shown
//                 }
//                 @Override
//                 public void onInterstitialShowFailed() {
//                     // Called when interstitial show failed
//                 }
//                 @Override
//                 public void onInterstitialClicked() {
//                     // Called when interstitial is clicked
//                 }
//                 @Override
//                 public void onInterstitialClosed() {
//                     // Called when interstitial is closed
//                     Appodeal.destroy(Appodeal.INTERSTITIAL);
//                 }
//                 @Override
//                 public void onInterstitialExpired()  {
//                     // Called when interstitial is expired
//                 }
//             });
//         }else{
//             Appodeal.initialize(activity, appodeal_app_key_, Appodeal.INTERSTITIAL, true);
//             if (AdProgress.dialog!=null)
//                 AdProgress.dismissAdProgress();
//         }
//     }

//     private static void AppodealBanner(Activity activity, LinearLayout ad_frame_, String appodeal_app_key) {
//         Appodeal.getBannerView(activity);
//         ad_frame_.addView(Appodeal.getBannerView(activity));
// //        Appodeal.setBannerViewId(R.id.appodealBannerView);
//         Appodeal.initialize(activity, appodeal_app_key, Appodeal.BANNER_VIEW, true);
//         Appodeal.setBannerCallbacks(new BannerCallbacks() {
//             @Override
//             public void onBannerLoaded(int height, boolean isPrecache) {
//                 // Called when banner is loaded
//                 Toast.makeText(activity, "onBannerLoaded", Toast.LENGTH_SHORT).show();
//                 Appodeal.show(activity, Appodeal.BANNER_VIEW);
//             }
//             @Override
//             public void onBannerFailedToLoad() {
//                 // Called when banner failed to load
//                 Toast.makeText(activity, "onBannerFailedToLoad", Toast.LENGTH_SHORT).show();
//             }
//             @Override
//             public void onBannerShown() {
//                 // Called when banner is shown
//                 Toast.makeText(activity, "onBannerShown", Toast.LENGTH_SHORT).show();
//             }
//             @Override
//             public void onBannerShowFailed() {
//                 // Called when banner show failed
//                 Toast.makeText(activity, "onBannerShowFailed", Toast.LENGTH_SHORT).show();
//             }
//             @Override
//             public void onBannerClicked() {
//                 // Called when banner is clicked
//                 Toast.makeText(activity, "onBannerClicked", Toast.LENGTH_SHORT).show();
//             }
//             @Override
//             public void onBannerExpired() {
//                 // Called when banner is expired
//                 Toast.makeText(activity, "onBannerExpired", Toast.LENGTH_SHORT).show();
//             }
//         });
//     }

    // private static void IronSourceBanner(Activity activity, LinearLayout ad_frame){
    //     IronSourceBannerLayout bannerLayout = IronSource.createBanner(activity, ISBannerSize.BANNER);
    //     LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
    //             LinearLayout.LayoutParams.WRAP_CONTENT);
    //     ad_frame.addView(bannerLayout, 0, layoutParams);
    //     bannerLayout.setBannerListener(new BannerListener() {
    //         @Override
    //         public void onBannerAdLoaded() {
    //             // Called after a banner ad has been successfully loaded
    //         }
    //         @Override
    //         public void onBannerAdLoadFailed(IronSourceError error) {
    //             // Called after a banner has attempted to load an ad but failed.
    //         }
    //         @Override
    //         public void onBannerAdClicked() {
    //             // Called after a banner has been clicked.
    //         }
    //         @Override
    //         public void onBannerAdScreenPresented() {
    //             // Called when a banner is about to present a full screen content.
    //         }
    //         @Override
    //         public void onBannerAdScreenDismissed() {
    //             // Called after a full screen content has been dismissed
    //         }
    //         @Override
    //         public void onBannerAdLeftApplication() {
    //             // Called when a user would be taken out of the application context.
    //         }
    //     });
    //     IronSource.loadBanner(bannerLayout);
    // }

    // private static void IronSourceInterstitial(){
    //     IronSource.setInterstitialListener(new InterstitialListener() {
    //         @Override
    //         public void onInterstitialAdReady() {
    //             IronSource.showInterstitial();
    //             if (AdProgress.dialog!=null)
    //                 AdProgress.dismissAdProgress();
    //         }
    //         @Override
    //         public void onInterstitialAdLoadFailed(IronSourceError error) {
    //             if (AdProgress.dialog!=null)
    //                 AdProgress.dismissAdProgress();
    //         }
    //         @Override
    //         public void onInterstitialAdOpened() {
    //         }
    //         @Override
    //         public void onInterstitialAdClosed() {
    //         }
    //         @Override
    //         public void onInterstitialAdShowFailed(IronSourceError error) {
    //             if (AdProgress.dialog!=null)
    //                 AdProgress.dismissAdProgress();
    //         }
    //         @Override
    //         public void onInterstitialAdClicked() {
    //         }
    //         @Override
    //         public void onInterstitialAdShowSucceeded() {
    //         }
    //     });
    //     IronSource.loadInterstitial();
    // }

    private static void AdmobBanner(Activity activity, LinearLayout ad_frame, String ad_unit_admob_banner_){
        com.google.android.gms.ads.AdView adViewAdmob = new com.google.android.gms.ads.AdView(activity);
        adViewAdmob.setAdSize(com.google.android.gms.ads.AdSize.FULL_BANNER);
        adViewAdmob.setAdUnitId(ad_unit_admob_banner_);
        ad_frame.addView(adViewAdmob);
        AdRequest adRequestAdmob = new AdRequest.Builder().build();
        adViewAdmob.loadAd(adRequestAdmob);
    }

    private static void AdmobInterstitial(Activity activity, String ad_unit_admob_interstitial_){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, ad_unit_admob_interstitial_, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.show(activity);
                        if (AdProgress.dialog!=null)
                            AdProgress.dismissAdProgress();
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                        if (AdProgress.dialog!=null)
                            AdProgress.dismissAdProgress();
                    }
                });
    }
}
