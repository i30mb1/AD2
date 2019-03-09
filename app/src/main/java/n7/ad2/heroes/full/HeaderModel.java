package n7.ad2.heroes.full;

public class HeaderModel implements Response {

    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
