package event.pojo;

public class BibleVersePositionEvent {

    private final int bibleVersePosition;

    public BibleVersePositionEvent(int bibleVersePosition) {
        this.bibleVersePosition = bibleVersePosition;
    }

    public int getBibleVersePosition() {
        return bibleVersePosition;
    }



}
