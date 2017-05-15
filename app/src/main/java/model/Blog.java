package model;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.japhethwaswa.church.BR;

public class Blog extends BaseObservable{

    String blog_id;
    String blog_title;
    String url_key;
    String image_url;
    String brief_description;
    String content;
    String author_name;
    String publish_date;
    String blog_category_id;
    String visible;
    String created_at;

    @Bindable
    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id;
        notifyPropertyChanged(BR.blog_id);
    }

    @Bindable
    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
        notifyPropertyChanged(BR.blog_title);
    }

    @Bindable
    public String getUrl_key() {
        return url_key;
    }

    public void setUrl_key(String url_key) {
        this.url_key = url_key;
        notifyPropertyChanged(BR.url_key);
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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    @Bindable
    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
        notifyPropertyChanged(BR.author_name);
    }

    @Bindable
    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
        notifyPropertyChanged(BR.publish_date);
    }

    @Bindable
    public String getBlog_category_id() {
        return blog_category_id;
    }

    public void setBlog_category_id(String blog_category_id) {
        this.blog_category_id = blog_category_id;
        notifyPropertyChanged(BR.blog_category_id);
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
}
