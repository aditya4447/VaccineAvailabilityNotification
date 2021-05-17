package in.omdev.vaccinenotif.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionManager;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.R;
import in.omdev.vaccinenotif.Util;
import in.omdev.vaccinenotif.api.SessionsAPI;
import in.omdev.vaccinenotif.component.CheckerService;
import in.omdev.vaccinenotif.databinding.FragmentServiceUiBinding;
import in.omdev.vaccinenotif.entity.Centers;
import in.omdev.vaccinenotif.entity.Links;
import in.omdev.vaccinenotif.model.MainViewModel;
import in.omdev.vaccinenotif.model.ServiceUIViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceUIFragment extends Fragment {

    private FragmentServiceUiBinding binding;
    private Animator loadingAnimator;
    private Animator successAnimation;
    private ServiceUIViewModel serviceUIViewModel;
    private FirebaseAnalytics analytics;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CheckerService checkerService = ((CheckerService.Binder) service)
                    .getService();
            checkerService.startChecking(mainViewModel);
            requireActivity().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private MainViewModel mainViewModel;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServiceUiBinding.inflate(getLayoutInflater(),
                container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (CheckerService.isStarted()) {
            Intent intent = new Intent(requireContext(), CheckerService.class);
            intent.putExtra(Const.KEY_STOP, true);
            requireContext().startService(intent);
        }
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        serviceUIViewModel = new ViewModelProvider(requireActivity()).get(ServiceUIViewModel.class);
        analytics = FirebaseAnalytics.getInstance(requireContext());
        loadingAnimator = AnimatorInflater.loadAnimator(requireContext(), R.animator.up_down);
        loadingAnimator.setTarget(binding.ivLoadingServiceUi);
        successAnimation = AnimatorInflater.loadAnimator(requireContext(), R.animator.rotate_in);
        successAnimation.setTarget(binding.ivLoadingServiceUi);
        if (serviceUIViewModel.getStatusLiveData().getValue()
                == ServiceUIViewModel.STATUS_SUCCESS) {
            setSuccess(serviceUIViewModel.getMessageLiveData().getValue(), false);
            if (!serviceUIViewModel.getMessageLiveData().getValue().isEmpty()) {
                binding.btnExitServiceUi.setVisibility(View.VISIBLE);
                binding.btnRegisterServiceUi.setVisibility(View.GONE);
            } else {
                binding.btnExitServiceUi.setVisibility(View.GONE);
                binding.btnRegisterServiceUi.setVisibility(View.VISIBLE);
            }
        } else if (serviceUIViewModel.getStatusLiveData().getValue()
                == ServiceUIViewModel.STATUS_FAILED) {
            setFailure(serviceUIViewModel.getMessageLiveData().getValue(), false);
        } else if (serviceUIViewModel.getStatusLiveData().getValue()
                == ServiceUIViewModel.STATUS_NONE) {
            startLoading();
            searchVaccines(mainViewModel);
        }
        serviceUIViewModel.getMessageLiveData().observe(getViewLifecycleOwner(), s ->
                binding.txtMessageServiceUi.setText(s));
        serviceUIViewModel.getSessionInfoLiveData().observe(getViewLifecycleOwner(), s -> {
            if (s == null) {
                s = "";
            }
            binding.txtSessionServiceUi.setText(s);
        });
        serviceUIViewModel.getStatusLiveData().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.getRoot());
                set.connect(R.id.cl_loading_parent, ConstraintSet.BOTTOM,
                        ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                TransitionManager.beginDelayedTransition(binding.getRoot());
                set.applyTo(binding.getRoot());
                binding.scrollSessionServiceUi.setVisibility(View.GONE);
                binding.scrollRetryServiceUi.setVisibility(View.GONE);
            } else if (integer == ServiceUIViewModel.VIEW_SESSION) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.getRoot());
                set.clear(R.id.cl_loading_parent, ConstraintSet.BOTTOM);
                TransitionManager.beginDelayedTransition(binding.getRoot());
                set.applyTo(binding.getRoot());
                binding.scrollSessionServiceUi.setVisibility(View.VISIBLE);
            } else if (integer == ServiceUIViewModel.VIEW_RETRY) {
                ConstraintSet set = new ConstraintSet();
                set.clone(binding.getRoot());
                set.clear(R.id.cl_loading_parent, ConstraintSet.BOTTOM);
                TransitionManager.beginDelayedTransition(binding.getRoot());
                set.applyTo(binding.getRoot());
                binding.scrollRetryServiceUi.setVisibility(View.VISIBLE);
            }
        });
        binding.btnExitServiceUi.setOnClickListener(v -> requireActivity().finish());
        binding.btnRetryServiceUi.setOnClickListener(v -> {
            mainViewModel.retry();
            serviceUIViewModel.setStatus(ServiceUIViewModel.STATUS_NONE);
        });
        Links links = mainViewModel.getLinkLiveData().getValue();
        if (links == null) {
            setFailure("Failed to get data.");
            return;
        }
        binding.btnRegisterServiceUi.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(links.getRegistration()))));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void searchVaccines(MainViewModel viewModel) {
        searchVaccines(viewModel, 0);
    }

    private void searchVaccines(MainViewModel viewModel, int time) {
        Links links = viewModel.getLinkLiveData().getValue();
        if (links == null) {
            setFailure("Failed to get data.");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, time * 7);
        Date newDate = new Date(calendar.getTimeInMillis());
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(newDate);
        Call<Centers> centersCall;
        SessionsAPI sessionsAPI = new Retrofit.Builder()
                .baseUrl(links.getBase())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SessionsAPI.class);
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
                if (!response.isSuccessful() || response.body() == null) {
                    setFailure("Failed to get data.");
                    return;
                }
                Centers.Center center = response.body().checkAvailability(viewModel);
                if (center != null) {
                    setSuccess("Vaccine available");
                    binding.btnExitServiceUi.setVisibility(View.GONE);
                    binding.btnRegisterServiceUi.setVisibility(View.VISIBLE);
                    Centers.Session session = response.body().getFoundSession();
                    serviceUIViewModel.setSessionInfo(Util.getSessionInfo(center, session));
                    serviceUIViewModel.setStatus(ServiceUIViewModel.VIEW_SESSION);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Const.EventParam.AVAILABLE_CAPACITY,
                            session.getAvailable_capacity());
                    bundle.putInt(Const.EventParam.MIN_AGE, session.getMin_age_limit());
                    bundle.putString(Const.EventParam.FEE, session.getFee());
                    bundle.putString(Const.EventParam.EVENT_SOURCE, "direct");
                    analytics.logEvent(Const.Event.VACCINE_AVAILABLE, bundle);
                } else if(time < 4) {
                    searchVaccines(viewModel, time + 1);
                } else {
                    Intent intent = new Intent(requireContext(), CheckerService.class);
                    requireActivity().startService(intent);
                    requireActivity().bindService(intent, connection,
                            Context.BIND_AUTO_CREATE);
                    binding.btnExitServiceUi.setVisibility(View.VISIBLE);
                    binding.btnRegisterServiceUi.setVisibility(View.GONE);
                    setSuccess("We will notify you when vaccine becomes available.");
                    serviceUIViewModel.setSessionInfo("");
                    serviceUIViewModel.setStatus(ServiceUIViewModel.VIEW_SESSION);
                    analytics.logEvent(Const.Event.SERVICE_STARTED, null);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Centers> call,
                                  @NotNull Throwable t) {
                setFailure("Failed to load data.");
            }
        });
    }

    public void startLoading() {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setRotation(20);
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_vaccine_bottle);
        serviceUIViewModel.setMessage("");
        loadingAnimator.start();
    }
    private void setFailure(String message) {
        setFailure(message, true);
    }

    private void setFailure(String message, boolean animate) {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_baseline_close_100);
        serviceUIViewModel.setMessage(message);
        serviceUIViewModel.setStatus(ServiceUIViewModel.STATUS_FAILED);
        if (animate) {
            successAnimation.start();
        }
        serviceUIViewModel.setStatus(ServiceUIViewModel.VIEW_RETRY);
    }

    private void setSuccess(String message) {
        setSuccess(message, true);
    }


    private void setSuccess(String message, boolean animate) {
        stopAllAnimations();
        binding.ivLoadingServiceUi.setImageResource(R.drawable.ic_baseline_check_100);
        serviceUIViewModel.setMessage(message);
        serviceUIViewModel.setStatus(ServiceUIViewModel.STATUS_SUCCESS);
        if (animate) {
            successAnimation.start();
        }
    }

    private void stopAllAnimations() {
        loadingAnimator.cancel();
        successAnimation.cancel();
        binding.ivLoadingServiceUi.setTranslationY(0f);
        binding.ivLoadingServiceUi.setRotation(0f);
        binding.ivLoadingServiceUi.setRotationY(0f);
    }
}