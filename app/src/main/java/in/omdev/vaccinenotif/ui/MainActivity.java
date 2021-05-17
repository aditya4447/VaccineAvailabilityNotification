package in.omdev.vaccinenotif.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.MobileAds;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;
import in.omdev.vaccinenotif.databinding.ActivityMainBinding;
import in.omdev.vaccinenotif.model.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getFormSubmittedLiveData().observe(this, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.container_main, ServiceUIFragment.class, null)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right)
                        .replace(R.id.container_main, MainFragment.class, null)
                        .commit();
            }
        });
        MobileAds.initialize(this);
        if (savedInstanceState == null && PreferenceManager
                .getDefaultSharedPreferences(this).getBoolean(Const.KEY_SHOW_DIALOG, true)) {
            new WelcomeDialog().show(fragmentManager, "welcome");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_info) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}