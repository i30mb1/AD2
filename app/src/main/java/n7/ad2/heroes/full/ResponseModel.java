package n7.ad2.heroes.full;

import android.databinding.ObservableBoolean;

public class ResponseModel implements Response {

    public ObservableBoolean playing = new ObservableBoolean(false);
    public ObservableBoolean inStore = new ObservableBoolean(false);
    private String href = "";
    private String title = "";
    private String icons = "";
    private String titleForFolder = "";

    public String getTitleForFolder() {
        return titleForFolder;
    }

    public String getIcons() {
        return icons;
    }

    public void setIcons(String icons) {
        this.icons = icons;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleForFolder = title.toLowerCase()
                .replace(" ", "_")
                .replace(".", "")
                .replace("!", "")
                .replace("?", "")
                .trim() + ".mp3";
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public int getType() {
        return Response.TYPE_RESPONSE;
    }
}
