package in.omdev.vaccinenotif;

public class Const {
    public static final String LOG_TAG = "vaccine4447";
    public static final String REF_PUBLIC = "public";
    public static final String REF_VACCINES = "vaccines";
    public static final String REF_FEE_TYPES = "fee_types";
    public static final String REF_LINKS = "links";
    public static final String KEY_INTERVAL = "interval";
    public static final String REF_WEEKS = "weeks";
    public static final String KEY_STOP = "stop";
    public static final int NOTIFICATION_ID_SERVICE = 1;
    public static final int NOTIFICATION_ID_AVAILABLE = 2;
    public static final int REQUEST_CODE_SERVICE = 1;
    public static final int REQUEST_CODE_AVAILABLE = 2;
    public static final String PREF_STATUS = "status";
    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_REG_LINK = "reg_link";
    public static final String KEY_SHOW_DIALOG = "show_dialog";

    public static class Event {
        public static final String RATE_APP = "rate_app";
        public static final String SEND_FEEDBACK = "send_feedback";
        public static final String HELP = "help";
        public static final String SUBMIT = "submit";
        public static final String VACCINE_AVAILABLE = "vaccine_available";
        public static final String SERVICE_STARTED = "service_started";
    }

    public static class EventParam {
        public static final String METHOD = "method";
        public static final String PIN = "pin";
        public static final String STATE_ID = "state_id";
        public static final String DISTRICT_ID = "district_id";
        public static final String AGE = "age";
        public static final String VACCINES = "vaccines";
        public static final String FEE_TYPES = "fee_types";
        public static final String AVAILABLE_CAPACITY = "available_capacity";
        public static final String MIN_AGE = "min_age";
        public static final String FEE = "fee";
        public static final String EVENT_SOURCE = "event_source";
    }
}
