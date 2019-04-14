package n7.ad2.twitch.retrofit;


import android.view.View;

public class Streams {
    private int viewers = 0;
    private Preview preview;
    private Channel channel;
    private boolean isPremium = false;

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public int getViewers() {
        return viewers;
    }

    public void setViewers(int viewers) {
        this.viewers = viewers;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void startStreamFull(View view) {
//        Intent intent = new Intent(view.getContext(), StreamsFullActivity.class);
//        intent.putExtra(CHANNEL_NAME, getChannel().getDisplay_name().toLowerCase());
//        intent.putExtra(CHANNEL_TITLE, getChannel().getStatus());
//        view.getContext().startActivity(intent);
    }
}
