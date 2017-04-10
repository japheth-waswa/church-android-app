package model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class BibleChapter extends BaseObservable{
    String chapter_number;
    String chapter_book_code;
    String chapter_code;

    @Bindable
    public String getChapter_number() {
        return chapter_number;
    }

    public void setChapter_number(String chapter_number) {
        this.chapter_number = chapter_number;
        notifyPropertyChanged(BR.chapter_number);
    }

    @Bindable
    public String getChapter_book_code() {
        return chapter_book_code;
    }

    public void setChapter_book_code(String chapter_book_code) {
        this.chapter_book_code = chapter_book_code;
        notifyPropertyChanged(BR.chapter_book_code);
    }

    @Bindable
    public String getChapter_code() {
        return chapter_code;
    }

    public void setChapter_code(String chapter_code) {
        this.chapter_code = chapter_code;
        notifyPropertyChanged(BR.chapter_code);
    }
}
