package n7.ad2.tournaments;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import n7.ad2.tournaments.db.TournamentGame;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class TournamentsWorker extends Worker {

    private static final String BASE_URL = "https://dota2.ru/esport/matches/?page=";
    public static final String DELETE_TABLE = "delete_table";
    public static final String PAGE = "page";
    public static final String TAG = "tournaments_worker_tag";

    public TournamentsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean deleteTable = getInputData().getBoolean(DELETE_TABLE, false);
        int page = getInputData().getInt(PAGE, 0);

        List<TournamentGame> gamesList = new ArrayList<>();

        try {
            String url = BASE_URL + page;
            Document doc = Jsoup.connect(url).get();
            Elements matchElements = doc.getElementsByClass("esport-match-single");
            for (int j = 0; j < matchElements.size(); j++) {
                TournamentGame game = new TournamentGame();
                game.url = "https://dota2.ru" + matchElements.get(j).child(0).attr("href");
                Elements matchElementsChildren = matchElements.get(j).child(0).children();
                for (int i = 0; i < matchElementsChildren.size(); i++) {
                    matchElementsChildren.get(i);
                    if (matchElementsChildren.get(i).attr("class").equals("team team-left")) {
                        for (Element element : matchElementsChildren.get(i).children()) {
                            if (element.attr("class").equals("name"))
                                game.team1Name = element.text();
                            if (element.tag().toString().equals("img"))
                                game.team1Logo = "https://dota2.ru" + element.attr("src");
                        }
                    }
                    if (matchElementsChildren.get(i).attr("class").equals("status")) {
                        for (Element element : matchElementsChildren.get(i).children()) {
                            if (element.attr("class").equals("score match-shop-result"))
                                game.teamScore = element.attr("data-value");
                            if (element.attr("class").equals("time")) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", new Locale("ru", "RU"));
                                Long matchStart = simpleDateFormat.parse(element.text().trim() + new SimpleDateFormat(".yyyy", new Locale("ru", "RU")).format(new Date())).getTime();
                                Long currentTime = simpleDateFormat.parse(simpleDateFormat.format(new Date())).getTime();
                                Long time = (matchStart - currentTime) / 1000;
                                game.teamTimeRemains = time;
                                game.teamTime = matchStart;
//                                String remain = String.format(Locale.US, "%02d:%02d", time / 3600, (time/60)%60);
                            }
                            if (element.attr("class").equals("live")) {
                                game.teamScore = "LIVE";
                            }
                        }
                    }
                    if (matchElementsChildren.get(i).attr("class").equals("team team-right")) {
                        for (Element element : matchElementsChildren.get(i).children()) {
                            if (element.attr("class").equals("name"))
                                game.team2Name = element.text();
                            if (element.tag().toString().equals("img"))
                                game.team2Logo = "https://dota2.ru" + element.attr("src");
                        }
                    }
                }
                gamesList.add(game);
            }
            if (gamesList.size() == 0) throw new Exception();
            GamesDao gamesDao = GamesRoomDatabase.getDatabase(getApplicationContext()).gamesDao();
            if (deleteTable) gamesDao.deleteAll();
            gamesDao.setGames(gamesList);
            getApplicationContext().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "page_"+page+"_loaded"));
            return Result.success();
        } catch (Exception e) {
            GamesDao gamesDao = GamesRoomDatabase.getDatabase(getApplicationContext()).gamesDao();
            gamesDao.deleteAllUnfinished();
            getApplicationContext().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "page_"+page+"_failed"));
        }
        return Result.failure();
    }
}
