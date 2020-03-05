package n7.ad2.items.db;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import n7.ad2.R;
import n7.ad2.utils.Utils;

@Database(entities = ItemModel.class, version = 113, exportSchema = false)
public abstract class ItemsRoomDatabase extends RoomDatabase {

    private static ItemsRoomDatabase INSTANCE;

    public static ItemsRoomDatabase getDatabase(final Context context, final Executor diskIO) {
        if (INSTANCE == null) {
            synchronized (ItemsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), ItemsRoomDatabase.class, "items113.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    diskIO.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (context.getResources().getString(R.string.language_resource)) {
                                                default:
                                                case "eng":
                                                    try {
                                                        JSONArray jsonHeroes = new JSONArray(Utils.readJSONFromAsset(context, "items.json"));
                                                        for (int i = 0; i < jsonHeroes.length(); i++) {
                                                            JSONObject jsonObject = jsonHeroes.getJSONObject(i);
                                                            ItemModel item = new ItemModel(jsonObject.getString("name"), jsonObject.getString("nameEng"));
                                                            getDatabase(context, diskIO).itemsDao().insert(item);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                            }
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ItemsDao itemsDao();

}
