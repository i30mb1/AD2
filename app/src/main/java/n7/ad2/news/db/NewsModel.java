package n7.ad2.news.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import n7.ad2.activity.NewsActivity;

import static n7.ad2.activity.NewsActivity.HREF;

@Entity
public class NewsModel {

    @PrimaryKey
    @NonNull
    private String href;
    private long date;
    private String content;
    private String title;
    private String imageHref;
    @Ignore
    private Boolean withImage;

    public NewsModel(@NonNull String href) {
        this.href = href;
    }

    public Boolean getWithImage() {
        return withImage;
    }

    public void setWithImage(Boolean withImage) {
        this.withImage = withImage;
    }

    @NonNull
    public String getHref() {
        return href;
    }

    public void setHref(@NonNull String href) {
        this.href = href;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageHref() {
        return imageHref;
    }

    public void setImageHref(String imageHref) {
        this.imageHref = imageHref;
    }

    public void startNewsFull(View view) {
        Intent intent = new Intent(view.getContext(), NewsActivity.class);
        intent.putExtra(HREF, href);
        view.getContext().startActivity(intent);
    }

}
