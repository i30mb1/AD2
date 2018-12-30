package n7.ad2.storage;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import n7.ad2.model.ResponseModel;
import n7.ad2.utils.Utils;

public class ResponsesStorage {

    private List<ResponseModel> list = new ArrayList<>();

    public ResponsesStorage(Context context, String path) {
        try {
            String jsonString = Utils.readJSONFromAsset(context, path);
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
        }
    }

    public List<ResponseModel> getData(int startPosition, int size) {
        List<ResponseModel> newList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (list.size() > startPosition) {
                newList.add(list.get(startPosition));
                ++startPosition;
            }
        }
        return newList;
    }

    public List<ResponseModel> getDataSearch(String search) {
        List<ResponseModel> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().toLowerCase().contains(search.toLowerCase().trim()))
                newList.add(list.get(i));
        }
        return newList;
    }

}
