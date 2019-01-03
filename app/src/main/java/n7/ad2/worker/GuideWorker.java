package n7.ad2.worker;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.Executors;

import androidx.work.Worker;
import n7.ad2.db.heroes.HeroModel;
import n7.ad2.db.heroes.HeroesDao;
import n7.ad2.db.heroes.HeroesRoomDatabase;

public class GuideWorker extends Worker {

    public static final String UNIQUE_WORK = "UNIQUE_GUIDE_WORK";
    public static final String HERO_CODE_NAME = "HERO_CODE_NAME";

    @NonNull
    @Override
    public Result doWork() {

        String heroCodeName = getInputData().getString(HERO_CODE_NAME);
        HeroesDao heroesDao = HeroesRoomDatabase.getDatabase(getApplicationContext(), Executors.newSingleThreadExecutor()).heroesDao();
        HeroModel heroes = heroesDao.getHeroByCodeNameObject(heroCodeName);

        StringBuilder stringWinRate = new StringBuilder();
        StringBuilder stringPickRate = new StringBuilder();
        StringBuilder stringLane = new StringBuilder();
        StringBuilder stringTime = new StringBuilder();
        StringBuilder stringStartingItems = new StringBuilder();
        StringBuilder stringFurtherItems = new StringBuilder();
        StringBuilder stringSkillBuilds = new StringBuilder();
        StringBuilder stringBestVersus = new StringBuilder();
        StringBuilder stringWorstVersus = new StringBuilder();

        try {
            Document documentSimple = Jsoup.connect("https://ru.dotabuff.com/heroes/" + heroCodeName.replace("_", "-")).get();
            // BEST VERSUS
            Elements best = documentSimple.getElementsByTag("tbody").get(3).children();
            for (Element element : best) {
                stringBestVersus.append(element.child(1).text().toLowerCase().trim().replace(" ", "_"))
                        .append("^").append(element.child(2).text()).append("/");
            }
            // WORST VERSUS
            Elements worst = documentSimple.getElementsByTag("tbody").get(4).children();
            for (Element element : worst) {
                stringWorstVersus.append(element.child(1).text().toLowerCase().trim().replace(" ", "_"))
                        .append("^").append(element.child(2).text()).append("/");
            }

            Document document = Jsoup.connect("https://www.dotabuff.com/heroes/" + heroCodeName.replace("_", "-") + "/guides").get();
            // WIN RATE
            Elements winrate = document.getElementsByClass("won");
            Elements loserate = document.getElementsByClass("lost");
            if (winrate.size() > 0) {
                stringWinRate.append(winrate.get(0).text());
            }
            if (loserate.size() > 0) {
                stringWinRate.append(loserate.get(0).text());
            }
            // PICK RATE
            Elements pickrate = document.getElementsByTag("dd");
            if (pickrate.size() > 0)
                stringPickRate.append(pickrate.get(0).text());
            // 5 DIFFERENT PACKS
            Elements elements = document.getElementsByClass("r-stats-grid");
            for (Element element : elements) {
                element.children();
                // TIME LAST PLAY
                Elements time = element.getElementsByTag("time");
                if (time.size() > 0) {
                    if (stringTime.length() != 0) {
                        stringTime.append("+");
                    }
                    stringTime.append(time.text());
                }
                // LANE
                if (stringLane.length() != 0) {
                    stringLane.append("+");
                }
                stringLane.append(detectLine(element));
                // ITEMS
                Elements elementsItemRows = element.child(2).getElementsByClass("kv r-none-mobile");
                // STARTING ITEMS
                Element startingItems = elementsItemRows.get(0);
//                if (stringStartingItems.length() != 0) {
//                    stringStartingItems.append("+");
//                }
//                for (Element item : startingItems.children()) {
//                    if (item.tag().toString().endsWith("div")) {
//                        stringStartingItems.append(item.child(0).child(0).child(0).attr("title").toLowerCase().trim().replace(" ", "_"));
//                        stringStartingItems.append("/");
//                    }
//                }
                // FURTHER ITEMS
                if (stringFurtherItems.length() != 0) {
                    stringFurtherItems.append("+");
                }
                for (int i = 0; i < elementsItemRows.size(); i++) {
                    boolean findTime = false;
                    for (Element item : elementsItemRows.get(i).children()) {
                        if (item.tag().toString().equals("div")) {
                            String itemName = item.child(0).child(0).child(0).attr("title").toLowerCase().trim().replace(" ", "_");
                            if (itemName.equals("gem_of_true_sight")) continue;
                            stringFurtherItems.append(itemName);
                            if (i != 0)
                                if (!findTime && elementsItemRows.get(i).getElementsByTag("small").size() > 0) {
                                    findTime = true;
                                    stringFurtherItems.append("^").append(elementsItemRows.get(i).getElementsByTag("small").get(0).text());
                                }
                        }
                        if (!stringFurtherItems.toString().endsWith("/"))
                            stringFurtherItems.append("/");
                    }
                }
                // SKILL BUILD
                if (stringSkillBuilds.length() != 0) {
                    stringSkillBuilds.append("+");
                }
                Elements skills = element.child(3).getElementsByClass("kv kv-small-margin");
                for (Element skill : skills) {
                    String skillName = skill.child(0).child(0).child(0).attr("alt");
                    if (skillName.startsWith("Talent:")) skillName = "talent";
                    stringSkillBuilds.append(skillName).append("/");
                }
            }
            // SAVE DATA
            heroes.setFurtherItems(stringFurtherItems.toString().replace("'", "%27"));
            heroes.setStartingItems(stringStartingItems.toString().replace("'", "%27"));
            heroes.setPickrate(stringPickRate.toString());
            heroes.setLane(stringLane.toString());
            heroes.setWinrate(stringWinRate.toString());
            heroes.setTime(stringTime.toString());
            heroes.setSkillBuilds(stringSkillBuilds.toString());
            heroes.setBestVersus(stringBestVersus.toString());
            heroes.setWorstVersus(stringWorstVersus.toString());

            heroesDao.update(heroes);

        } catch (IOException e) {
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }

    private String detectLine(Element element) {
        Elements elementsMid = element.getElementsByClass("fa fa-lane-midlane fa-fw lane-icon midlane-icon");
        if (elementsMid.size() == 1) return "MID LANE";
        Elements elementsOff = element.getElementsByClass("fa fa-lane-offlane fa-fw lane-icon offlane-icon");
        if (elementsOff.size() == 1) return "OFF LANE";
        Elements elementsSafe = element.getElementsByClass("fa fa-lane-safelane fa-fw lane-icon safelane-icon");
        if (elementsSafe.size() == 1) return "SAFE LANE";
        Elements elementsRoaming = element.getElementsByClass("fa fa-lane-roaming fa-fw lane-icon roaming-icon");
        if (elementsRoaming.size() == 1) return "ROAMING";
        return "-";
    }

}
