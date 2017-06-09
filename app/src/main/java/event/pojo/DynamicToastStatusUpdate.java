package event.pojo;

public class DynamicToastStatusUpdate {

    int status;
    String statusMessage;

    //0-info,1-warning,2-success,3-error
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
