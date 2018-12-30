
package n7.ad2.retrofit.steamNews;

import java.util.List;

public class Appnews {

    private Integer appid;
    private List<NewsItem> newsitems = null;
    private Integer count;

    public Integer getAppid() {
        return appid;
    }

    public void setAppid(Integer appid) {
        this.appid = appid;
    }

    public List<NewsItem> getNewsitems() {
        return newsitems;
    }

    public void setNewsitems(List<NewsItem> newsitems) {
        this.newsitems = newsitems;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
