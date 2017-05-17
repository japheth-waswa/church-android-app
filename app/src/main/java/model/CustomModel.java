package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class CustomModel extends BaseObservable{

    String category;
    String comments_count;

    @Bindable
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        notifyPropertyChanged(BR.category);
    }

    @Bindable
    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
        notifyPropertyChanged(BR.comments_count);
    }
}
