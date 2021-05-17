package in.omdev.vaccinenotif.entity;

import java.util.Locale;

public class Links {
    private String base;
    private String states;
    private String districts;
    private String sessionsByDistrict;
    private String sessionsByPin;
    private String Registration;

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }

    public String getFormattedDistricts(int state_id) {
        return String.format(Locale.getDefault(), districts, state_id);
    }

    public String getSessionsByDistrict() {
        return sessionsByDistrict;
    }

    public void setSessionsByDistrict(String sessionsByDistrict) {
        this.sessionsByDistrict = sessionsByDistrict;
    }

    public String getSessionsByPin() {
        return sessionsByPin;
    }

    public void setSessionsByPin(String sessionsByPin) {
        this.sessionsByPin = sessionsByPin;
    }

    public String getRegistration() {
        return Registration;
    }

    public void setRegistration(String registration) {
        Registration = registration;
    }
}
