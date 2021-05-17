package in.omdev.vaccinenotif.entity;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.Nullable;
import in.omdev.vaccinenotif.model.MainViewModel;

public class Centers {

    private ArrayList<Center> centers;
    private Session foundSession;


    public ArrayList<Center> getCenters() {
        return centers;
    }

    public void setCenters(ArrayList<Center> centers) {
        this.centers = centers;
    }

    public Session getFoundSession() {
        return foundSession;
    }

    public void setFoundSession(Session foundSession) {
        this.foundSession = foundSession;
    }

    @Nullable
    public Center checkAvailability(MainViewModel viewModel) {
        if (centers == null || centers.isEmpty()) {
            return null;
        }
        for (Center center : centers) {
            for (Session session : center.getSessions()) {
                if (session.getMin_age_limit() > viewModel.getAge()) {
                    continue;
                }
                if (!viewModel.getSelectedVaccines().isEmpty()) {
                    if (!viewModel.getSelectedVaccines().contains(session.getVaccine())) {
                        continue;
                    }
                }
                if (!viewModel.getSelectedFeeTypes().isEmpty()) {
                    if (!viewModel.getSelectedFeeTypes().contains(center.getFee_type())) {
                        continue;
                    }
                }
                if (session.getAvailable_capacity() < 1) {
                    continue;
                }
                setFoundSession(session);
                return center;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String toString() {
        return "Centers{" +
                "centers=" + centers +
                '}';
    }

    public static class Center {
        private String center_id;
        private String name;
        private String name_l;
        private String address;
        private String address_l;
        private String state_name;
        private String state_name_l;
        private String district_name;
        private String district_name_l;
        private String block_name;
        private String block_name_l;
        private String pincode;
        private int lat;
        @SerializedName("long")
        private int longitude;
        private String from;
        private String to;
        private String fee_type;
        private VaccineFee[] vaccine_fees;
        private ArrayList<Session> sessions;


        public String getCenter_id() {
            return center_id;
        }

        public void setCenter_id(String center_id) {
            this.center_id = center_id;
        }

        public String getName_l() {
            return name_l;
        }

        public void setName_l(String name_l) {
            this.name_l = name_l;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress_l() {
            return address_l;
        }

        public void setAddress_l(String address_l) {
            this.address_l = address_l;
        }

        public String getState_name() {
            return state_name;
        }

        public void setState_name(String state_name) {
            this.state_name = state_name;
        }

        public String getState_name_l() {
            return state_name_l;
        }

        public void setState_name_l(String state_name_l) {
            this.state_name_l = state_name_l;
        }

        public String getDistrict_name() {
            return district_name;
        }

        public void setDistrict_name(String district_name) {
            this.district_name = district_name;
        }

        public String getDistrict_name_l() {
            return district_name_l;
        }

        public void setDistrict_name_l(String district_name_l) {
            this.district_name_l = district_name_l;
        }

        public String getBlock_name() {
            return block_name;
        }

        public void setBlock_name(String block_name) {
            this.block_name = block_name;
        }

        public String getBlock_name_l() {
            return block_name_l;
        }

        public void setBlock_name_l(String block_name_l) {
            this.block_name_l = block_name_l;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public int getLat() {
            return lat;
        }

        public void setLat(int lat) {
            this.lat = lat;
        }

        public int getLongitude() {
            return longitude;
        }

        public void setLongitude(int longitude) {
            this.longitude = longitude;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFee_type() {
            return fee_type;
        }

        public void setFee_type(String fee_type) {
            this.fee_type = fee_type;
        }

        public VaccineFee[] getVaccine_fees() {
            return vaccine_fees;
        }

        public void setVaccine_fees(VaccineFee[] vaccine_fees) {
            this.vaccine_fees = vaccine_fees;
        }

        public ArrayList<Session> getSessions() {
            return sessions;
        }

        public void setSessions(ArrayList<Session> sessions) {
            this.sessions = sessions;
        }


        @NotNull
        @Override
        public String toString() {
            return "Centers{" +
                    "center_id='" + center_id + '\'' +
                    ", name_l='" + name_l + '\'' +
                    ", address='" + address + '\'' +
                    ", address_l='" + address_l + '\'' +
                    ", state_name='" + state_name + '\'' +
                    ", state_name_l='" + state_name_l + '\'' +
                    ", district_name='" + district_name + '\'' +
                    ", district_name_l='" + district_name_l + '\'' +
                    ", block_name='" + block_name + '\'' +
                    ", block_name_l='" + block_name_l + '\'' +
                    ", pincode='" + pincode + '\'' +
                    ", lat=" + lat +
                    ", longitude=" + longitude +
                    ", from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", fee_type='" + fee_type + '\'' +
                    ", vaccine_fees=" + Arrays.toString(vaccine_fees) +
                    ", sessions=" + sessions +
                    '}';
        }
    }

    public static class Session {
        private String fee;
        private String session_id;
        private String date;
        private int available_capacity;
        private int available_capacity_dose1;
        private int available_capacity_dose2;
        private int min_age_limit;
        private String vaccine;
        private ArrayList<String> slots;

        public String getFee() {
            return fee;
        }

        public void setFee(String fee) {
            this.fee = fee;
        }

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getAvailable_capacity() {
            return available_capacity;
        }

        public void setAvailable_capacity(int available_capacity) {
            this.available_capacity = available_capacity;
        }

        public int getAvailable_capacity_dose1() {
            return available_capacity_dose1;
        }

        public void setAvailable_capacity_dose1(int available_capacity_dose1) {
            this.available_capacity_dose1 = available_capacity_dose1;
        }

        public int getAvailable_capacity_dose2() {
            return available_capacity_dose2;
        }

        public void setAvailable_capacity_dose2(int available_capacity_dose2) {
            this.available_capacity_dose2 = available_capacity_dose2;
        }

        public int getMin_age_limit() {
            return min_age_limit;
        }

        public void setMin_age_limit(int min_age_limit) {
            this.min_age_limit = min_age_limit;
        }

        public String getVaccine() {
            return vaccine;
        }

        public void setVaccine(String vaccine) {
            this.vaccine = vaccine;
        }

        public ArrayList<String> getSlots() {
            return slots;
        }

        public void setSlots(ArrayList<String> slots) {
            this.slots = slots;
        }

        @NotNull
        @Override
        public String toString() {
            return "Session{" +
                    "fee='" + fee + '\'' +
                    ", session_id='" + session_id + '\'' +
                    ", date='" + date + '\'' +
                    ", available_capacity=" + available_capacity +
                    ", available_capacity_dose1=" + available_capacity_dose1 +
                    ", available_capacity_dose2=" + available_capacity_dose2 +
                    ", min_age_limit=" + min_age_limit +
                    ", vaccine='" + vaccine + '\'' +
                    ", slots=" + slots +
                    '}';
        }
    }

    public static class VaccineFee {
        private String vaccine;
        private int fee;

        public String getVaccine() {
            return vaccine;
        }

        public void setVaccine(String vaccine) {
            this.vaccine = vaccine;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }

        @NotNull
        @Override
        public String toString() {
            return "VaccineFee{" +
                    "vaccine='" + vaccine + '\'' +
                    ", fee=" + fee +
                    '}';
        }
    }
}
