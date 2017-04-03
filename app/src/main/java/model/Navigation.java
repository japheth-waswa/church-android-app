package model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class Navigation extends BaseObservable{

    String drawable;
    String title;
    String description;
    String color;

    @Bindable
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyPropertyChanged(BR.color);
    }



    @Bindable
    public String getDrawable() {
        return drawable;
    }

    public void setDrawable(String drawable) {
        this.drawable = drawable;
        notifyPropertyChanged(BR.drawable);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

}
