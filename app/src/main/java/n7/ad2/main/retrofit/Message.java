package n7.ad2.main.retrofit;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("twitch")
    private String twitch;
    @SerializedName("en")
    private String en;
    @SerializedName("ru")
    private String ru;
    @SerializedName("n7message")
    private String n7message;
    @SerializedName("updateFromMarket")
    private boolean updateFromMarket = true;

    public String getTwitch() {
        return twitch;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getN7message() {
        return n7message;
    }

    public void setN7message(String n7message) {
        this.n7message = n7message;
    }

    public boolean isUpdateFromMarket() {
        return updateFromMarket;
    }

    public void setUpdateFromMarket(boolean updateFromMarket) {
        this.updateFromMarket = updateFromMarket;
    }
}
