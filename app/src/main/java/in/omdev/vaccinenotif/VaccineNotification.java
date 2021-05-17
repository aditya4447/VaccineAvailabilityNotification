package in.omdev.vaccinenotif;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;

import androidx.core.app.NotificationManagerCompat;

public class VaccineNotification extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationChannel channel = new NotificationChannel(
                getString(R.string._channel_id_service),
                getString(R.string._channel_name_service),
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(getString(R.string._channel_description_service));
        notificationManager.createNotificationChannel(channel);
        channel = new NotificationChannel(
                getString(R.string._channel_id_availability),
                getString(R.string._channel_name_availability),
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(getString(R.string._channel_description_availability));
        notificationManager.createNotificationChannel(channel);
    }
}
