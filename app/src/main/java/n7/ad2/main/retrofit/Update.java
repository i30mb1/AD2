package n7.ad2.main.retrofit;

import com.google.gson.annotations.SerializedName;

public class Update {

    // ставим всегда или бабка умрёт
    @SerializedName("versionCode")
    private int versionCode;

    @SerializedName("message")
    private Message message;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
