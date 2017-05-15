package event.pojo;

public class CurrentPositionEvent {

    private final int position;

    public CurrentPositionEvent(int position) {
        this.position = position;
    }
    public int getPosition() {
        return position;
    }

}
