package in.omdev.vaccinenotif.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;
import in.omdev.vaccinenotif.databinding.ActivityAboutBinding;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AboutActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById(R.id.imageView_app_icon_about);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            binding.textViewAppVersion.setText(pInfo.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.helpContainer.setOnClickListener(v -> {
            firebaseAnalytics.logEvent(Const.Event.HELP, null);
            new WelcomeDialog().show(getSupportFragmentManager(), null);
        });
        binding.rateAppContainer.setOnClickListener(v -> {
            firebaseAnalytics.logEvent(Const.Event.RATE_APP, null);
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string._play_store))));
        });
        binding.sendFeedbackContainer.setOnClickListener(v -> {
            firebaseAnalytics.logEvent(Const.Event.SEND_FEEDBACK, null);
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:"+getString(R.string._email_address)));
            try {
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, R.string._error_no_email_app, Toast.LENGTH_LONG).show();
            }
        });
        binding.ossContainer.setOnClickListener(v ->
                startActivity(new Intent(this, OssLicensesMenuActivity.class)));
    }

}