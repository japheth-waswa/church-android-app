package event.pojo;

public class BibleChapterPositionEvent {

    private final int bibleChapterPosition;

    public BibleChapterPositionEvent(int bibleChapterPosition) {
        this.bibleChapterPosition = bibleChapterPosition;
    }

    public int getBibleChapterPosition() {
        return bibleChapterPosition;
    }



}
