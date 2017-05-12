package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class Schedule extends BaseObservable{

    String schedule_id;
    String theme_title;
    String theme_description;
    String sunday_date;

    @Bindable
    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
        notifyPropertyChanged(BR.schedule_id);
    }

    @Bindable
    public String getTheme_title() {
        return theme_title;
    }

    public void setTheme_title(String theme_title) {
        this.theme_title = theme_title;
        notifyPropertyChanged(BR.theme_title);
    }

    @Bindable
    public String getTheme_description() {
        return theme_description;
    }

    public void setTheme_description(String theme_description) {
        this.theme_description = theme_description;
        notifyPropertyChanged(BR.theme_description);
    }

    @Bindable
    public String getSunday_date() {
        return sunday_date;
    }

    public void setSunday_date(String sunday_date) {
        this.sunday_date = sunday_date;
        notifyPropertyChanged(BR.sunday_date);
    }
}
