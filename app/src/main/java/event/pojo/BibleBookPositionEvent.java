package event.pojo;

public class BibleBookPositionEvent {

    private final int bibleBookPosition;

    public BibleBookPositionEvent(int bibleBookPosition) {
        this.bibleBookPosition = bibleBookPosition;
    }

    public int getBibleBookPosition() {
        return bibleBookPosition;
    }

}
