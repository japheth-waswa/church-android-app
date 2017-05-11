package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class Event extends BaseObservable{

    String event_id;
    String event_title;
    String image_url;
    String brief_description;
    String event_content;
    String event_date;
    String event_location;
    String event_category_id;
    String visible;
    String created_at;
    String updated_at;

    @Bindable
    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
        notifyPropertyChanged(BR.event_id);
    }

    @Bindable
    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
        notifyPropertyChanged(BR.event_title);
    }

    @Bindable
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
        notifyPropertyChanged(BR.image_url);
    }

    @Bindable
    public String getBrief_description() {
        return brief_description;
    }

    public void setBrief_description(String brief_description) {
        this.brief_description = brief_description;
        notifyPropertyChanged(BR.brief_description);
    }

    @Bindable
    public String getEvent_content() {
        return event_content;
    }

    public void setEvent_content(String event_content) {
        this.event_content = event_content;
        notifyPropertyChanged(BR.event_content);
    }

    @Bindable
    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
        notifyPropertyChanged(BR.event_date);
    }

    @Bindable
    public String getEvent_location() {
        return event_location;
    }

    public void setEvent_location(String event_location) {
        this.event_location = event_location;
        notifyPropertyChanged(BR.event_location);
    }

    @Bindable
    public String getEvent_category_id() {
        return event_category_id;
    }

    public void setEvent_category_id(String event_category_id) {
        this.event_category_id = event_category_id;
        notifyPropertyChanged(BR.event_category_id);
    }

    @Bindable
    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
        notifyPropertyChanged(BR.visible);
    }

    @Bindable
    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
        notifyPropertyChanged(BR.created_at);
    }

    @Bindable
    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
        notifyPropertyChanged(BR.updated_at);
    }
}
