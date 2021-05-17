package in.omdev.vaccinenotif.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;
import in.omdev.vaccinenotif.component.CheckerService;
import in.omdev.vaccinenotif.databinding.ActivityServiceUiBinding;

public class ServiceUIActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityServiceUiBinding binding;
    private Animator loadingAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServiceUiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingAnimator = AnimatorInflater.loadAnimator(this, R.animator.up_down);
        loadingAnimator.setTarget(binding.ivLoadingServiceUi);
        SharedPreferences preferences = getSharedPreferences(Const.PREF_STATUS, MODE_PRIVATE);
        binding.btnRegisterServiceUi.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(preferences.getString(
                        Const.KEY_REG_LINK, "https://selfregistration.cowin.gov.in/"
                )))));
        binding.btnStop.setOnClickListener(v -> {
            Intent i = new Intent(this, CheckerService.class);
            i.putExtra(Const.KEY_STOP, true);
            startService(i);
            new Handler().postDelayed(() -> {
                onSharedPreferenceChanged(preferences, Const.PREF_STATUS);
                binding.btnStop.setVisibility(View.GONE);
            }, 500);
        });
        onSharedPreferenceChanged(preferences, Const.PREF_STATUS);
        preferences.registerOnSharedPreferenceChangeListener(this);
        MobileAds.initialize(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adServiceUi.loadAd(adRequest);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!key.equals(Const.PREF_STATUS) || !CheckerService.isStarted()) {
            if (!CheckerService.isStarted()) {
                setFailure("Currently not checking for vaccine availability");
            }
            return;
        }
        binding.scrollSessionServiceUi.setVisibility(View.GONE);
        binding.btnStop.setVisibility(View.GONE);
        int status = sharedPreferences.getInt(Const.KEY_STATUS, CheckerService.STATUS_NONE);
        if (status == CheckerService.STATUS_NONE) {
            setLoading();
            binding.btnStop.setVisibility(View.VISIBLE);
        } else if (status == CheckerService.STATUS_AVAILABLE) {
            binding.scrollSessionServiceUi.setVisibility(View.GONE);
            setSuccess(getString(R.string.vaccine_available));
            binding.scrollSessionServiceUi.setVisibility(View.VISIBLE);
            binding.txtSessionServiceUi.setText(sharedPreferences.getString(Const.KEY_MESSAGE, ""));
            Intent i = new Intent(this, CheckerService.class);
            i.putExtra(Const.KEY_STOP, true);
            startService(i);

        } else if (status == CheckerService.STATUS_FAILED) {
            setFailure("Last status: failed - "
                    + sharedPreferences.getString(Const.KEY_MESSAGE, ""));
        }
    }

    private void setLoading() {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_vaccine_bottle);
        binding.ivLoadingServiceUi.setRotation(20);
        binding.txtMessageServiceUi2.setText(R.string.checking_availability_);
        loadingAnimator.start();
    }

    private void setFailure(String message) {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_baseline_close_100);
        binding.txtMessageServiceUi2.setText(message);
    }

    private void setSuccess(String message) {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_baseline_check_100);
        binding.txtMessageServiceUi2.setText(message);
    }

    private void stopAllAnimations() {

        loadingAnimator.cancel();
        binding.ivLoadingServiceUi.setTranslationY(0f);
        binding.ivLoadingServiceUi.setRotation(0f);
        binding.ivLoadingServiceUi.setRotationY(0f);
    }
}