package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class SchedulePage extends BaseObservable{

    String schedule_pages_id;
    String page_content;
    String sunday_schedule_id;
    String page_order;

    @Bindable
    public String getSchedule_pages_id() {
        return schedule_pages_id;
    }

    public void setSchedule_pages_id(String schedule_pages_id) {
        this.schedule_pages_id = schedule_pages_id;
        notifyPropertyChanged(BR.schedule_pages_id);
    }

    @Bindable
    public String getPage_content() {
        return page_content;
    }

    public void setPage_content(String page_content) {
        this.page_content = page_content;
        notifyPropertyChanged(BR.page_content);
    }

    @Bindable
    public String getSunday_schedule_id() {
        return sunday_schedule_id;
    }

    public void setSunday_schedule_id(String sunday_schedule_id) {
        this.sunday_schedule_id = sunday_schedule_id;
        notifyPropertyChanged(BR.sunday_schedule_id);
    }

    @Bindable
    public String getPage_order() {
        return page_order;
    }

    public void setPage_order(String page_order) {
        this.page_order = page_order;
        notifyPropertyChanged(BR.page_order);
    }
}
