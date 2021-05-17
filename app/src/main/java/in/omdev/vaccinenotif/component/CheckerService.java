package in.omdev.vaccinenotif.component;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;
import in.omdev.vaccinenotif.Util;
import in.omdev.vaccinenotif.api.SessionsAPI;
import in.omdev.vaccinenotif.entity.Centers;
import in.omdev.vaccinenotif.entity.Links;
import in.omdev.vaccinenotif.model.MainViewModel;
import in.omdev.vaccinenotif.ui.ServiceUIActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckerService extends Service {

    public static final int STATUS_NONE = 0;
    public static final int STATUS_FAILED = -1;
    public static final int STATUS_AVAILABLE = 1;

    private static boolean started = false;
    private final Binder binder = new Binder();
    private MainViewModel viewModel;
    private boolean mStop = false;
    private Handler handler;
    private int interval = 60000;
    private SessionsAPI sessionsAPI;
    private Links links;
    private Notification alertNotification;
    private SharedPreferences preferences;
    private NotificationManagerCompat notificationManager;
    private int time = 0;
    private int times = 4;


    public static boolean isStarted() {
        return started;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        started = true;
        FirebaseDatabase.getInstance().getReference()
                .child(Const.REF_PUBLIC)
                .child(Const.REF_WEEKS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Integer times1 = snapshot.getValue(Integer.class);
                        if (times1 != null) {
                            times = times1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child(Const.REF_PUBLIC).child(Const.KEY_INTERVAL).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Integer interval1 = snapshot.getValue(Integer.class);
                        if (interval1 != null) {
                            interval = interval1;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(Const.KEY_STOP)) {
            stopForeground(true);
            stopChecking();
            started = false;
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }

    public void startChecking(MainViewModel viewModel) {
        this.viewModel = viewModel;
        Drawable drawable = ContextCompat.getDrawable(this,R.mipmap.ic_launcher_round);

        Bitmap bitmap = Util.getBitmapFromDrawable(drawable);
        Notification notification = new NotificationCompat.Builder(this,
                getString(R.string._channel_id_service))
                .setContentTitle(getString(R.string.vaccine))
                .setContentText(getString(R.string._service_notification_content))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bitmap)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        Const.REQUEST_CODE_SERVICE,
                        new Intent(this, ServiceUIActivity.class),
                        0
                ))
                .build();
        startForeground(Const.NOTIFICATION_ID_SERVICE, notification);
        alertNotification = new NotificationCompat.Builder(this,
                getString(R.string._channel_id_availability))
                .setContentTitle(getString(R.string.vaccine_available))
                .setContentText(getString(R.string._vaccine_now_available))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(bitmap)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        Const.REQUEST_CODE_AVAILABLE,
                        new Intent(this, ServiceUIActivity.class),
                        0
                ))
                .setAutoCancel(true)
                .build();
        notificationManager = NotificationManagerCompat.from(this);
        links = viewModel.getLinkLiveData().getValue();
        if (links == null) {
            return;
        }
        sessionsAPI = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(viewModel.getLinkLiveData().getValue().getBase())
                .build()
                .create(SessionsAPI.class);
        handler = new Handler();
        handler.postDelayed(this::check, interval);
        preferences = getSharedPreferences(Const.PREF_STATUS, MODE_PRIVATE);
        preferences.edit()
                .remove(Const.KEY_MESSAGE)
                .remove(Const.KEY_STATUS)
                .putString(Const.KEY_REG_LINK, links.getRegistration())
                .apply();
    }

    public void stopChecking() {
        if (handler != null) {
            handler.removeCallbacks(this::check);
        }
        mStop = true;
    }

    public void setFailure(String message) {
        preferences
                .edit()
                .putInt(Const.KEY_STATUS, STATUS_FAILED)
                .putString(Const.KEY_MESSAGE, message)
                .apply();
    }

    public void setSuccess(String message) {
        preferences
                .edit()
                .putInt(Const.KEY_STATUS, STATUS_AVAILABLE)
                .putString(Const.KEY_MESSAGE, message)
                .apply();
        stopChecking();
    }

    private void check() {
        if (mStop) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, time * 7);
        Date newDate = new Date(calendar.getTimeInMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(newDate);
        Call<Centers> centersCall;
        if (viewModel.getMethod() == MainViewModel.Method.PIN) {
            if (viewModel.getPin() == null || viewModel.getPin().isEmpty()) {
                setFailure("Failed to get data.");
                return;
            }
            centersCall = sessionsAPI.getSessionsByPin(
                    links.getSessionsByPin(),
                    viewModel.getPin(),
                    date
            );
        } else {
            if (viewModel.getDistrict_id() == 0) {
                setFailure("Failed to get data.");
                return;
            }
            centersCall = sessionsAPI.getSessionsByDistrict(
                    links.getSessionsByDistrict(),
                    viewModel.getDistrict_id(),
                    date
            );
        }
        centersCall.enqueue(new Callback<Centers>() {
            @Override
            public void onResponse(@NotNull Call<Centers> call,
                                   @NotNull Response<Centers> response) {
                if (mStop) {
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    setFailure("Failed to get data.");
                    return;
                }
                Centers.Center center = response.body().checkAvailability(viewModel);
                if (center != null) {
                    Centers.Session session = response.body().getFoundSession();
                    setSuccess(Util.getSessionInfo(center, session));
                    notificationManager.notify(Const.NOTIFICATION_ID_AVAILABLE, alertNotification);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.EventParam.AVAILABLE_CAPACITY,
                            session.getAvailable_capacity());
                    bundle.putInt(Const.EventParam.MIN_AGE, session.getMin_age_limit());
                    bundle.putString(Const.EventParam.FEE, session.getFee());
                    bundle.putString(Const.EventParam.EVENT_SOURCE, "service");
                    FirebaseAnalytics.getInstance(CheckerService.this)
                            .logEvent(Const.Event.VACCINE_AVAILABLE, bundle);
                }
                if (time < times) {
                    time++;
                    check();
                } else {
                    handler.postDelayed(CheckerService.this::check, interval);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Centers> call,
                                  @NotNull Throwable t) {
                setFailure("Failed to load data.");
            }
        });
    }

    @Override
    public void onDestroy() {
        stopChecking();
        started = false;
        super.onDestroy();
    }

    public class Binder extends android.os.Binder {
        public CheckerService getService() {
            return CheckerService.this;
        }
    }
}