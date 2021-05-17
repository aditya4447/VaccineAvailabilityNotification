package in.omdev.vaccinenotif.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import in.omdev.vaccinenotif.Const;
import in.omdev.vaccinenotif.entity.Districts;
import in.omdev.vaccinenotif.entity.Links;
import in.omdev.vaccinenotif.entity.States;

public class MainViewModel extends ViewModel {
    private Method method = Method.PIN;
    private String pin;
    private int state_id;
    private int district_id;
    private int age = 45;
    private int weeks = 4;
    private final VaccinesLiveData vaccines = new VaccinesLiveData();
    private final FeeTypesLiveData fee_types = new FeeTypesLiveData();
    private final HashSet<String> selectedVaccines = new HashSet<>();
    private final HashSet<String> selectedFeeTypes = new HashSet<>();
    private final LinkLiveData linkLiveData = new LinkLiveData();
    private final MutableLiveData<States> statesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Hashtable<Integer, Districts>> districtsLiveData
            = new MutableLiveData<>(new Hashtable<>());
    private final MutableLiveData<Boolean> formSubmittedLiveData
            = new MutableLiveData<>(false);

    public MainViewModel() {
        FirebaseDatabase.getInstance().getReference().child(Const.REF_PUBLIC)
                .child(Const.REF_WEEKS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Integer i = snapshot.getValue(Integer.class);
                        if (i != null) {
                            weeks = i;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(int district_id) {
        this.district_id = district_id;
    }

    public VaccinesLiveData getVaccines() {
        return vaccines;
    }


    public HashSet<String> getSelectedVaccines() {
        return selectedVaccines;
    }

    public LinkLiveData getLinkLiveData() {
        return linkLiveData;
    }

    public MutableLiveData<States> getStatesLiveData() {
        return statesLiveData;
    }

    public void setStates(States states) {
        statesLiveData.postValue(states);
    }

    public MutableLiveData<Hashtable<Integer, Districts>> getDistrictsLiveData() {
        return districtsLiveData;
    }

    public void setDistricts(int state_id, Districts districts) {
        //noinspection ConstantConditions
        districtsLiveData.getValue().put(state_id, districts);
        districtsLiveData.setValue(districtsLiveData.getValue());
    }

    public HashSet<String> getSelectedFeeTypes() {
        return selectedFeeTypes;
    }

    public FeeTypesLiveData getFee_types() {
        return fee_types;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public MutableLiveData<Boolean> getFormSubmittedLiveData() {
        return formSubmittedLiveData;
    }

    public void submitForm() {
        formSubmittedLiveData.setValue(true);
    }

    public void retry() {
        formSubmittedLiveData.setValue(false);
    }

    @NonNull
    @Override
    public String toString() {
        return "MainViewModel{" +
                "method=" + method +
                ", pin='" + pin + '\'' +
                ", state_id=" + state_id +
                ", district_id=" + district_id +
                ", vaccines=" + vaccines.getValue() +
                ", fee_types=" + fee_types.getValue() +
                ", selectedVaccines=" + selectedVaccines +
                ", selectedFeeTypes=" + selectedFeeTypes +
                ", linkLiveData=" + linkLiveData.getValue() +
                ", statesLiveData=" + statesLiveData.getValue() +
                ", districtsLiveData=" + districtsLiveData.getValue() +
                ", formSubmittedLiveData=" + formSubmittedLiveData.getValue() +
                '}';
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeeks() {
        return weeks;
    }

    public enum Method {
        PIN, DISTRICT
    }

    public static class LinkLiveData extends MutableLiveData<Links> {

        public final DatabaseReference linksRef;
        public final StatesLinkListener listener;

        public LinkLiveData() {
            super();
            linksRef = FirebaseDatabase.getInstance().getReference()
                    .child(Const.REF_PUBLIC)
                    .child(Const.REF_LINKS);
            listener = new StatesLinkListener();
        }


        @Override
        protected void onActive() {
            if (getValue() == null) {
                linksRef.addListenerForSingleValueEvent(listener);
            }
        }

        private class StatesLinkListener implements ValueEventListener {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setValue(snapshot.getValue(Links.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
    }

    public static class VaccinesLiveData extends MutableLiveData<ArrayList<String>> {

        public final DatabaseReference vaccinesRef;
        public final VaccinesListener listener;

        public VaccinesLiveData() {
            super();
            vaccinesRef = FirebaseDatabase.getInstance().getReference()
                    .child(Const.REF_PUBLIC)
                    .child(Const.REF_VACCINES);
            listener = new VaccinesListener();
        }


        @Override
        protected void onActive() {
            if (getValue() == null) {
                vaccinesRef.addListenerForSingleValueEvent(listener);
            }
        }

        private class VaccinesListener implements ValueEventListener {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //noinspection unchecked
                    setValue((ArrayList<String>) snapshot.getValue());
                } else {
                    setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
    }

    public static class FeeTypesLiveData extends MutableLiveData<ArrayList<String>> {

        public final DatabaseReference feeTypesRef;
        public final VaccinesListener listener;

        public FeeTypesLiveData() {
            super();
            feeTypesRef = FirebaseDatabase.getInstance().getReference()
                    .child(Const.REF_PUBLIC)
                    .child(Const.REF_FEE_TYPES);
            listener = new VaccinesListener();
        }


        @Override
        protected void onActive() {
            if (getValue() == null) {
                feeTypesRef.addListenerForSingleValueEvent(listener);
            }
        }

        private class VaccinesListener implements ValueEventListener {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //noinspection unchecked
                    setValue((ArrayList<String>) snapshot.getValue());
                } else {
                    setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }
    }
}
