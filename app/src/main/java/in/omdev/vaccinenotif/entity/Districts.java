package in.omdev.vaccinenotif.entity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class Districts {

    private ArrayList<District> districts;
    private int ttl;

    public ArrayList<District> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<District> districts) {
        this.districts = districts;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public static class District {
        private int state_id;
        private int district_id;
        private String district_name;
        private String district_name_l;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            District district = (District) o;
            return district_id == district.district_id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(state_id, district_id);
        }

        @NotNull
        @Override
        public String toString() {
            return "District{" +
                    ", district_id=" + district_id +
                    '}';
        }
    }
}
