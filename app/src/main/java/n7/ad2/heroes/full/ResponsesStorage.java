package n7.ad2.heroes.full;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.Executor;

import n7.ad2.utils.Utils;

public class ResponsesStorage {

    public Invalidate invalidate;
    private LinkedList<ResponseModel> list = new LinkedList<>();
    private Executor diskIO;
    private Application application;
    private String path;

    ResponsesStorage(Application application, String path, Executor diskIO) {
        this.application = application;
        this.path = path;
        this.diskIO = diskIO;
    }

    public void setInvalidate(Invalidate invalidate) {
        this.invalidate = invalidate;
    }

    public void load() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                try {
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                    String jsonString = Utils.readJSONFromAsset(application, path);
                    if (jsonString == null) return;
                    JSONArray jsonArray = new JSONArray(jsonString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectSection = jsonArray.getJSONObject(i);
                        ResponseModel section = new ResponseModel();
                        section.setTitle(jsonObjectSection.getString("name"));
                        list.add(section);
                        JSONArray jsonArrayResponses = jsonObjectSection.getJSONArray("responses");
                        for (int ii = 0; ii < jsonArrayResponses.length(); ii++) {
                            ResponseModel response = new ResponseModel();
                            response.setTitle(jsonArrayResponses.getJSONObject(ii).getString("title"));
                            response.setHref(jsonArrayResponses.getJSONObject(ii).getString("href"));
                            if (jsonArrayResponses.getJSONObject(ii).has("icon"))
                                response.setIcons(jsonArrayResponses.getJSONObject(ii).getString("icon"));
                            list.add(response);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (invalidate != null) {
                        invalidate.invalidate();
                    }
                }
            }
        });
    }

    LinkedList<ResponseModel> getData(int startPosition, int size) {
        LinkedList<ResponseModel> newList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            if (list.size() > startPosition) {
                newList.add(list.get(startPosition));
                ++startPosition;
            }
        }
        return newList;
    }

    LinkedList<ResponseModel> getDataSearch(String search) {
        LinkedList<ResponseModel> newList = new LinkedList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().toLowerCase().contains(search.toLowerCase().trim()))
                newList.add(list.get(i));
        }
        return newList;
    }

}
