package n7.ad2.heroes.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

import n7.ad2.R;
import n7.ad2.utils.Utils;

@Database(entities = {HeroModel.class}, version = 113, exportSchema = false)
public abstract class HeroesRoomDatabase extends RoomDatabase {

    public abstract HeroesDao heroesDao();

    private static HeroesRoomDatabase INSTANCE;

    public static HeroesRoomDatabase getDatabase(final Context context, final Executor diskIO) {
        if (INSTANCE == null) {
            synchronized (HeroesRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(), HeroesRoomDatabase.class, "heroes113.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                // вызывается только при создании новой базы данных
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    diskIO.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (context.getResources().getString(R.string.language_resource)) {
                                                default:
                                                case "ru":
                                                case "eng":
                                                    try {
                                                        JSONArray jsonHeroes = new JSONArray(Utils.readJSONFromAsset(context, "heroes.json"));
                                                        for (int i = 0; i < jsonHeroes.length(); i++) {
                                                            JSONObject jsonObject = jsonHeroes.getJSONObject(i);
                                                            HeroModel heroes = new HeroModel(jsonObject.getString("name"), jsonObject.getString("nameEng"));
                                                            if (jsonObject.getString("name").equals("roshan")) {
                                                                heroes.setSkillBuilds("Spell Block/Bash/Slam+Spell Block/Bash/Slam+Spell Block/Bash/Slam+Spell Block/Bash/Slam+Spell Block/Bash/Slam");
                                                                heroes.setTime("fresh+fresh+fresh+fresh+fresh");
                                                                heroes.setPickrate("0%");
                                                                heroes.setWinrate("0%");
                                                                heroes.setLane("cave+cave+cave+cave+cave");
                                                                heroes.setWorstVersus("ursa^100%");
                                                                heroes.setBestVersus("techies^0%");
                                                                heroes.setStartingItems("aegis_of_the_immortal+aegis_of_the_immortal+aegis_of_the_immortal+aegis_of_the_immortal+aegis_of_the_immortal");
                                                                heroes.setFurtherItems("refresher_shard+cheese/refresher_shard+cheese/refresher_shard+cheese/refresher_shard+cheese/refresher_shard+cheese/refresher_shard");
                                                            }
                                                            getDatabase(context, diskIO).heroesDao().insert(heroes);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
//                                                case "zh":
//                                                    try {
//                                                        JSONArray jsonHeroes = new JSONArray(new Utils().readJSONFromAsset(context, "heroes.json"));
//                                                        for (int i = 0; i < jsonHeroes.length(); i++) {
//                                                            JSONObject jsonObject = jsonHeroes.getJSONObject(i);
//                                                            HeroModel heroes = new HeroModel(jsonObject.getString("name"), jsonObject.getString("nameZh"));
//                                                            if (jsonObject.getString("name").equals("roshan")) {
//                                                                heroes.setSkillBuilds("Spell Block/Bash/Slam/Strength of the Immortal+Spell Block/Bash/Slam/Strength of the Immortal+Spell Block/Bash/Slam/Strength of the Immortal+Spell Block/Bash/Slam/Strength of the Immortal+Spell Block/Bash/Slam/Strength of the Immortal");
//                                                                heroes.setTime("fresh+fresh+fresh+fresh+fresh");
//                                                                heroes.setPickrate("0%");
//                                                                heroes.setWinrate("0%");
//                                                                heroes.setLane("cave/cave/cave/cave/cave");
//                                                                heroes.setWorstVersus("ursa^100%+ursa^100%+ursa^100%+ursa^100%+ursa^100%");
//                                                                heroes.setStartingItems("Aegis of the Immortal+Aegis of the Immortal+Aegis of the Immortal+Aegis of the Immortal+Aegis of the Immortal");
//                                                                heroes.setFurtherItems("Cheese/Refresher Shard+Cheese/Refresher Shard+Cheese/Refresher Shard+Cheese/Refresher Shard+Cheese/Refresher Shard");
//                                                            }
//                                                            getDatabase(context,appExecutors).heroesDao().insert(heroes);
//                                                        }
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                    break;
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
}
