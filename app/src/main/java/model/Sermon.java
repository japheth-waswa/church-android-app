package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class Sermon extends BaseObservable{

    String sermon_id;
    String sermon_title;
    String sermon_image_url;
    String sermon_brief_description;
    String sermon_audio_url;
    String sermon_video_url;
    String sermon_pdf_url;
    String sermon_date;
    String sermon_visible;
    String sermon_created_at;
    String sermon_updated_at;

    @Bindable
    public String getSermon_id() {
        return sermon_id;
    }

    public void setSermon_id(String sermon_id) {
        this.sermon_id = sermon_id;
        notifyPropertyChanged(BR.sermon_id);

    }

    @Bindable
    public String getSermon_title() {
        return sermon_title;
    }

    public void setSermon_title(String sermon_title) {
        this.sermon_title = sermon_title;
        notifyPropertyChanged(BR.sermon_title);
    }

    @Bindable
    public String getSermon_image_url() {
        return sermon_image_url;
    }

    public void setSermon_image_url(String sermon_image_url) {
        this.sermon_image_url = sermon_image_url;
        notifyPropertyChanged(BR.sermon_image_url);
    }

    @Bindable
    public String getSermon_brief_description() {
        return sermon_brief_description;
    }

    public void setSermon_brief_description(String sermon_brief_description) {
        this.sermon_brief_description = sermon_brief_description;
        notifyPropertyChanged(BR.sermon_brief_description);
    }

    @Bindable
    public String getSermon_audio_url() {
        return sermon_audio_url;
    }

    public void setSermon_audio_url(String sermon_audio_url) {
        this.sermon_audio_url = sermon_audio_url;
        notifyPropertyChanged(BR.sermon_audio_url);
    }

    @Bindable
    public String getSermon_video_url() {
        return sermon_video_url;
    }

    public void setSermon_video_url(String sermon_video_url) {
        this.sermon_video_url = sermon_video_url;
        notifyPropertyChanged(BR.sermon_video_url);
    }

    @Bindable
    public String getSermon_pdf_url() {
        return sermon_pdf_url;
    }

    public void setSermon_pdf_url(String sermon_pdf_url) {
        this.sermon_pdf_url = sermon_pdf_url;
        notifyPropertyChanged(BR.sermon_pdf_url);
    }

    @Bindable
    public String getSermon_date() {
        return sermon_date;
    }

    public void setSermon_date(String sermon_date) {
        this.sermon_date = sermon_date;
        notifyPropertyChanged(BR.sermon_date);
    }

    @Bindable
    public String getSermon_visible() {
        return sermon_visible;
    }

    public void setSermon_visible(String sermon_visible) {
        this.sermon_visible = sermon_visible;
        notifyPropertyChanged(BR.sermon_visible);
    }

    @Bindable
    public String getSermon_created_at() {
        return sermon_created_at;
    }

    public void setSermon_created_at(String sermon_created_at) {
        this.sermon_created_at = sermon_created_at;
        notifyPropertyChanged(BR.sermon_created_at);
    }

    @Bindable
    public String getSermon_updated_at() {
        return sermon_updated_at;
    }

    public void setSermon_updated_at(String sermon_updated_at) {
        this.sermon_updated_at = sermon_updated_at;
        notifyPropertyChanged(BR.sermon_updated_at);

    }



}
