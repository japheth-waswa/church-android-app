package model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class BibleVerse extends BaseObservable{
    String verse_number;
    String verse_chapter_code;
    String verse;

    @Bindable
    public String getVerse_number() {
        return verse_number;
    }

    public void setVerse_number(String verse_number) {
        this.verse_number = verse_number;
        notifyPropertyChanged(BR.verse_number);
    }

    @Bindable
    public String getVerse_chapter_code() {
        return verse_chapter_code;
    }

    public void setVerse_chapter_code(String verse_chapter_code) {
        this.verse_chapter_code = verse_chapter_code;
        notifyPropertyChanged(BR.verse_chapter_code);
    }

    @Bindable
    public String getVerse() {
        return verse;
    }

    public void setVerse(String verse) {
        this.verse = verse;
        notifyPropertyChanged(BR.verse);
    }
}
