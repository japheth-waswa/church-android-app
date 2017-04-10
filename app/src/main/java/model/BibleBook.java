package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class BibleBook extends BaseObservable{
    String book_version;
    String book_code;
    String book_number;
    String book_name;

    @Bindable
    public String getBook_version() {
        return book_version;
    }

    public void setBook_version(String book_version) {
        this.book_version = book_version;
        notifyPropertyChanged(BR.book_version);
    }

    @Bindable
    public String getBook_code() {
        return book_code;
    }

    public void setBook_code(String book_code) {
        this.book_code = book_code;
        notifyPropertyChanged(BR.book_code);
    }

    @Bindable
    public String getBook_number() {
        return book_number;
    }

    public void setBook_number(String book_number) {
        this.book_number = book_number;
        notifyPropertyChanged(BR.book_number);
    }

    @Bindable
    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
        notifyPropertyChanged(BR.book_name);
    }


}
