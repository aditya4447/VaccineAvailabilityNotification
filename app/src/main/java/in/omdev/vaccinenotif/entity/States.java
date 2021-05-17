package in.omdev.vaccinenotif.entity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class States {

    private ArrayList<State> states;
    private int ttl;

    public ArrayList<State> getStates() {
        return states;
    }

    public void setStates(ArrayList<State> states) {
        this.states = states;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public static class State {
        private int state_id;
        private String state_name;
        private String state_name_l;

        public int getState_id() {
            return state_id;
        }

        public void setState_id(int state_id) {
            this.state_id = state_id;
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

        @NotNull
        @Override
        public String toString() {
            if (state_name_l != null) {
                return state_name_l;
            } else if (state_name != null) {
                return state_name;
            } else {
                return "";
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return state_id == state.state_id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(state_id);
        }
    }
}
