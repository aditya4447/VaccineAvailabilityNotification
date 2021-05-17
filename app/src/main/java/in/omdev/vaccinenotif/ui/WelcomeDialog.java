package in.omdev.vaccinenotif.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;

public class WelcomeDialog extends DialogFragment {

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle(R.string.welcome)
                .setMessage(R.string._welcome_message)
                .setPositiveButton(R.string.start, (dialog1, which) -> dismiss())
                .create();
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit()
                .putBoolean(Const.KEY_SHOW_DIALOG, false)
                .apply();
        super.onDismiss(dialog);
    }
}
