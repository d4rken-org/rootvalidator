/*
 * Project Root Validator
 *
 * @link https://github.com/d4rken/rootvalidator
 * @license https://github.com/d4rken/rootvalidator/blob/master/LICENSE GPLv3
 */

package eu.thedarken.rootvalidator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private Fragment mFragment;
    private AdView mAdView;
    private FrameLayout mAdContainer;
    private static final String FILE_NO_ADS = "no_ads";

    public String getFragmentClass() {
        return ValidatorFragment.class.getName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_mainactivity_layout);
        mFragment = getSupportFragmentManager().findFragmentByTag(getFragmentClass());
        if (mFragment == null) {
            mFragment = Fragment.instantiate(this, getFragmentClass());
            getSupportFragmentManager().beginTransaction().replace(R.id.content, mFragment, getFragmentClass()).commit();
        }
        mAdContainer = (FrameLayout) findViewById(R.id.fl_ad_container);
        File noAdsFile = new File(getExternalFilesDir(null), FILE_NO_ADS);
        if (!noAdsFile.exists()) {
            mAdContainer.setVisibility(View.VISIBLE);
            mAdView = (AdView) findViewById(R.id.adv_banner);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("C34CF836138E576F0C3CC8BBF6B19388")
                    .addTestDevice("A6A278A2F9CF28BD949FC2265AEAE62F")
                    .build();
            mAdView.loadAd(adRequest);
        } else {
            mAdContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        if (mAdView != null)
            mAdView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mAdView != null)
            mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null)
            mAdView.destroy();
        super.onDestroy();
    }
}