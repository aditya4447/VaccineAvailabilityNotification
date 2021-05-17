package in.omdev.vaccinenotif.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ServiceUIViewModel extends ViewModel {

    public static final int STATUS_NONE = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = -1;

    public static final int VIEW_SESSION = 1;
    public static final int VIEW_RETRY = 2;

    private final StatusLiveData statusLiveData = new StatusLiveData();
    private final MessageLiveData messageLiveData = new MessageLiveData();
    private final MutableLiveData<String> sessionInfoLiveData = new MutableLiveData<>();
    private final ViewLiveData viewLiveData = new ViewLiveData();

    public StatusLiveData getStatusLiveData() {
        return statusLiveData;
    }

    public void setStatus(int status) {
        statusLiveData.setValue(status);
    }

    public MessageLiveData getMessageLiveData() {
        return messageLiveData;
    }

    public void setMessage(String message) {
        messageLiveData.setValue(message);
    }

    public MutableLiveData<String> getSessionInfoLiveData() {
        return sessionInfoLiveData;
    }

    public void setSessionInfo(String sessionInfo) {
        sessionInfoLiveData.setValue(sessionInfo);
    }

    public ViewLiveData getViewLiveData() {
        return viewLiveData;
    }

    public static class StatusLiveData extends MutableLiveData<Integer> {
        public StatusLiveData() {
            super(0);
        }

        @NonNull
        @Override
        public Integer getValue() {
            Integer i = super.getValue();
            if (i == null) {
                return 0;
            }
            return i;
        }
    }

    public static class MessageLiveData extends MutableLiveData<String> {
        public MessageLiveData() {
            super("");
        }

        @NonNull
        @Override
        public String getValue() {
            String s = super.getValue();
            if (s == null) {
                return "";
            }
            return s;
        }
    }

    public static class ViewLiveData extends MutableLiveData<Integer> {
        public ViewLiveData() {
            super(0);
        }

        @NonNull
        @Override
        public Integer getValue() {
            Integer i = super.getValue();
            if (i == null) {
                return 0;
            }
            return i;
        }
    }
}
