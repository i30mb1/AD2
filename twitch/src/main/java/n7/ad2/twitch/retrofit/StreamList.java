package n7.ad2.twitch.retrofit;

import java.util.List;

public class StreamList {

    private int _total;
    private List<Streams> streams;

    public int get_total() {
        return _total;
    }

    public void set_total(int _total) {
        this._total = _total;
    }

    public List<Streams> getStreams() {
        return streams;
    }

    public void setStreams(List<Streams> streams) {
        this.streams = streams;
    }

}
