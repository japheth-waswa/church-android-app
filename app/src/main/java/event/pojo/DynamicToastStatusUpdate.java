package event.pojo;

public class DynamicToastStatusUpdate {

    int status;
    String statusMessage;

    public DynamicToastStatusUpdate(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }


}
