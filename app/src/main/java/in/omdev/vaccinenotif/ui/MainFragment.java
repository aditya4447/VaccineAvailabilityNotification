package in.omdev.vaccinenotif.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Hashtable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.api.MetadataAPI;
import in.omdev.vaccinenotif.databinding.FragmentMainBinding;
import in.omdev.vaccinenotif.databinding.ItemDistrictBinding;
import in.omdev.vaccinenotif.databinding.ItemPinBinding;
import in.omdev.vaccinenotif.entity.Districts;
import in.omdev.vaccinenotif.entity.Links;
import in.omdev.vaccinenotif.entity.States;
import in.omdev.vaccinenotif.model.MainViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment {


    private static final int TOTAL_TABS = 2;
    private static final int POSITION_PIN = 0;
    private static final int POSITION_DISTRICT = 1;
    private FragmentMainBinding binding;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MainViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adMain.loadAd(adRequest);
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == POSITION_PIN) {
                    return PINFragment.newInstance();
                } else {
                    return DistrictFragment.newInstance();
                }
            }

            @Override
            public int getItemCount() {
                return TOTAL_TABS;
            }
        };
        binding.viewPagerMain.setOffscreenPageLimit(2);
        binding.viewPagerMain.setAdapter(adapter);
        binding.viewPagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabsMain.selectTab(binding.tabsMain.getTabAt(position), true);
            }
        });
        binding.tabsMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == POSITION_PIN) {
                    binding.viewPagerMain.setCurrentItem(POSITION_PIN, true);
                    viewModel.setMethod(MainViewModel.Method.PIN);
                } else if (tab.getPosition() == POSITION_DISTRICT) {
                    binding.viewPagerMain.setCurrentItem(POSITION_DISTRICT, true);
                    viewModel.setMethod(MainViewModel.Method.DISTRICT);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.textViewAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    viewModel.setAge(Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    viewModel.setAge(45);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        viewModel.getVaccines().observe(getViewLifecycleOwner(), s -> {
            binding.chipsVaccines.removeAllViews();
            if (s == null) {
                return;
            }
            for (String vaccine : s) {
                Chip chip = new Chip(requireContext());
                chip.setText(vaccine);
                chip.setCheckable(true);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        viewModel.getSelectedVaccines().add(buttonView.getText().toString());
                    } else {
                        viewModel.getSelectedVaccines().remove(buttonView.getText().toString());
                    }
                });
                if (viewModel.getSelectedVaccines().contains(vaccine)) {
                    chip.setChecked(true);
                }
                binding.chipsVaccines.addView(chip, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });
        viewModel.getFee_types().observe(getViewLifecycleOwner(), s -> {
            binding.chipsFeeType.removeAllViews();
            if (s == null) {
                return;
            }
            for (String fee_type : s) {
                Chip chip = new Chip(requireContext());
                chip.setText(fee_type);
                chip.setCheckable(true);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        viewModel.getSelectedFeeTypes().add(buttonView.getText().toString());
                    } else {
                        viewModel.getSelectedFeeTypes().remove(buttonView.getText().toString());
                    }
                });
                if (viewModel.getSelectedFeeTypes().contains(fee_type)) {
                    chip.setChecked(true);
                }
                binding.chipsFeeType.addView(chip, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });
        binding.btnSubmit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt(Const.EventParam.METHOD,
                    viewModel.getMethod() == MainViewModel.Method.PIN ? 1 : 2);
            bundle.putString(Const.EventParam.PIN, viewModel.getPin());
            bundle.putInt(Const.EventParam.STATE_ID, viewModel.getState_id());
            bundle.putInt(Const.EventParam.DISTRICT_ID, viewModel.getDistrict_id());
            bundle.putInt(Const.EventParam.AGE, viewModel.getAge());
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            bundle.putStringArray(Const.EventParam.VACCINES,
                        viewModel.getSelectedVaccines()
                                .toArray(new String[viewModel.getSelectedVaccines().size()]));
            //noinspection ToArrayCallWithZeroLengthArrayArgument
            bundle.putStringArray(Const.EventParam.FEE_TYPES,
                    viewModel.getSelectedFeeTypes()
                            .toArray(new String[viewModel.getSelectedFeeTypes().size()]));
            firebaseAnalytics.logEvent(Const.Event.SUBMIT, bundle);
            viewModel.submitForm();
        });
    }

    public static class PINFragment extends Fragment {

        private ItemPinBinding pinBinding;

        public static PINFragment newInstance() {
            return new PINFragment();
        }

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            pinBinding = ItemPinBinding.inflate(inflater, container, false);
            return pinBinding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull @NotNull View view,
                                  @Nullable Bundle savedInstanceState) {
            MainViewModel viewModel = new ViewModelProvider(requireActivity())
                    .get(MainViewModel.class);
            if (viewModel.getPin() != null) {
                pinBinding.textViewPin.setText(viewModel.getPin());
            }
            pinBinding.textViewPin.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    viewModel.setPin(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public static class DistrictFragment extends Fragment {

        private ItemDistrictBinding districtBinding;

        public static DistrictFragment newInstance() {
            return new DistrictFragment();
        }

        @Override
        public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            districtBinding = ItemDistrictBinding.inflate(inflater, container, false);
            return districtBinding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull @NotNull View view,
                                  @Nullable Bundle savedInstanceState) {
            MainViewModel viewModel = new ViewModelProvider(requireActivity())
                    .get(MainViewModel.class);
            final ArrayList<String> districtsList = new ArrayList<>();
            final ArrayList<String> statesList = new ArrayList<>();
            ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    statesList
            );
            districtBinding.spinnerStates.setAdapter(statesAdapter);
            ArrayAdapter<String> districtsAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    districtsList
            );
            districtBinding.spinnerDistricts.setAdapter(districtsAdapter);
            viewModel.getStatesLiveData().observe(getViewLifecycleOwner(), states -> {
                statesList.clear();
                if (states != null) {
                    for (States.State state : states.getStates()) {
                        statesList.add(state.getState_name());
                    }
                }
                int selection = viewModel.getState_id();
                statesAdapter.notifyDataSetChanged();
                if (selection != 0 && states != null) {
                    States.State state = new States.State();
                    state.setState_id(selection);
                    districtBinding.spinnerStates.setSelection(states.getStates()
                            .indexOf(state), false);
                }
            });
            districtBinding.spinnerStates.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id) {
                            States states = viewModel.getStatesLiveData().getValue();
                            if (states == null) {
                                return;
                            }
                            int state_id = states.getStates().get(position).getState_id();
                            if (viewModel.getState_id() == state_id) {
                                return;
                            }
                            viewModel.setState_id(state_id);
                            Hashtable<Integer, Districts> districtsHashtable =
                                    viewModel.getDistrictsLiveData().getValue();
                            if (viewModel.getState_id() == 0) {
                                districtsList.clear();
                                statesAdapter.notifyDataSetChanged();
                            }
                            if (districtsHashtable == null) {
                                return;
                            }
                            if (districtsHashtable.contains(viewModel.getState_id())) {
                                Districts districts = districtsHashtable
                                        .get(viewModel.getState_id());
                                if (districts == null) {
                                    return;
                                }
                                for (Districts.District district : districts.getDistricts()) {
                                    districtsList.clear();
                                    districtsList.add(district.getDistrict_name());
                                }
                            } else {
                                districtsList.clear();
                                loadDistricts(viewModel);
                            }
                            districtsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            viewModel.setState_id(0);
                        }
                    });
            viewModel.getDistrictsLiveData()
                    .observe(getViewLifecycleOwner(), districtsHashtable -> {
                        if (viewModel.getState_id() == 0
                                || !districtsHashtable.containsKey(viewModel.getState_id())) {
                            districtsList.clear();
                        } else {
                            districtsList.clear();
                            Districts districts = districtsHashtable
                                    .get(viewModel.getState_id());
                            if (districts != null) {
                                for (Districts.District district : districts.getDistricts()) {
                                    districtsList.add(district.getDistrict_name());
                                }
                            }
                        }
                        int selection = viewModel.getDistrict_id();
                        districtsAdapter.notifyDataSetChanged();
                        if (selection != 0) {
                            Districts.District district = new Districts.District();
                            district.setDistrict_id(selection);
                            Districts districts = districtsHashtable.get(viewModel.getState_id());
                            if (districts == null) {
                                return;
                            }
                            int index = districts.getDistricts().indexOf(district);
                            if (index != -1) {
                                districtBinding.spinnerDistricts.setSelection(index, false);
                            }
                            getActivity();
                        }
                    });
            districtBinding.spinnerDistricts.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                            Hashtable<Integer, Districts> districtsHashtable
                                    = viewModel.getDistrictsLiveData().getValue();
                            if (districtsHashtable == null) {
                                return;
                            }
                            Districts districts = districtsHashtable.get(viewModel.getState_id());
                            if (districts == null) {
                                return;
                            }
                            viewModel.setDistrict_id(districts.getDistricts()
                                    .get(position).getDistrict_id());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            viewModel.setDistrict_id(0);
                        }
                    });
            viewModel.getLinkLiveData().observe(getViewLifecycleOwner(), links -> {
                if (viewModel.getStatesLiveData().getValue() != null) {
                    return;
                }
                new Retrofit.Builder()
                        .baseUrl(links.getBase())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(MetadataAPI.class)
                        .getStates(links.getStates())
                        .enqueue(new Callback<States>() {
                            @Override
                            public void onResponse(@NotNull Call<States> call,
                                                   @NotNull Response<States> response) {
                                if (response.isSuccessful()) {
                                    viewModel.setStates(response.body());
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<States> call,
                                                  @NotNull Throwable t) {
                                Toast.makeText(requireContext(), "Failed to get states list",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

        private void loadDistricts(MainViewModel viewModel) {
            Links links = viewModel.getLinkLiveData().getValue();
            if (links == null) {
                return;
            }
            int state_id = viewModel.getState_id();
            new Retrofit.Builder()
                    .baseUrl(links.getBase())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MetadataAPI.class)
                    .getDistricts(links.getFormattedDistricts(state_id))
                    .enqueue(new Callback<Districts>() {
                        @Override
                        public void onResponse(@NotNull Call<Districts> call,
                                               @NotNull Response<Districts> response) {
                            if (response.isSuccessful()) {
                                viewModel.setDistricts(state_id, response.body());
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<Districts> call,
                                              @NotNull Throwable t) {
                            Toast.makeText(requireContext(), "Failed to get districts list",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}