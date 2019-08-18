import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

class DotaHeroesParser {

    public static void main(String[] args) {

        String reHeroes[] = new String[]{"undying"};

        int count = 0;
        int size = loadHeroesFromFile().size();
        for (String hero : loadHeroesFromFile()) {
            String counter = String.format(Locale.US, "% d/%d ", ++count, size);
//            loadResponses(hero, counter);
            loadSpellsAndDescription(hero, counter);
        }

//        loadZhItems(false);
//        loadRusItems(false);
//        loadEngItems(false);
    }

    private static void loadZhItems(boolean loadImage) {
        int COUNT_GROUP_OF_ITEMS = 13;
        BufferedImage image = null;
        String itemName = "";
        try {
            String zh = "https://dota2-zh.gamepedia.com";
            String eng = "https://dota2.gamepedia.com";
            String ru = "https://dota2-ru.gamepedia.com";
            Document docRus = Jsoup.connect("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B").get();

            int count = 0;
            JSONArray jsonArrayItems = new JSONArray();
            JSONObject jsonObjectSection;


            for (Element itemGroup : docRus.getElementsByClass("itemlist")) {
                count++;
                if (count >= COUNT_GROUP_OF_ITEMS) break;
                for (Element item : itemGroup.children()) {
                    itemName = item.child(0).attr("href");

                    if (itemName.startsWith("/Animal_Courier")) continue;
                    if (itemName.startsWith("/Power_Treads")) continue;


                    String itemNameImage = itemName.replaceAll("\\(.+\\)", "").trim().replace(" ", "_").replace("\\\\", "");
                    jsonObjectSection = new JSONObject();
                    jsonObjectSection.put("name", itemNameImage.toLowerCase().substring(1));
                    jsonObjectSection.put("nameEng", itemNameImage.substring(1).replace("_", " ").trim());
//                    jsonObjectSection.put("href", itemName);

                    JSONObject jsonObjectItem = new JSONObject();

                    JSONArray jsonArrayAbilities = new JSONArray();//ряды для способностей
                    Document docItemDescription;
                    if (jsonObjectSection.get("nameEng").equals("Ogre Axe")) {
                        docItemDescription = Jsoup.connect(zh + "/%E9%A3%9F%E4%BA%BA%E9%AD%94%E4%B9%8B%E6%96%A7").get();
                    } else if (jsonObjectSection.get("nameEng").equals("Refresher Shard")) {
                        docItemDescription = Jsoup.connect(zh + "/%E5%88%B7%E6%96%B0%E7%90%83%E7%A2%8E%E7%89%87").get();
                    } else {
                        docItemDescription = Jsoup.connect(zh + "/" + jsonObjectSection.get("nameEng").toString().replace(" ", "_")).get();
                    }
//                    Document docItemDescription = Jsoup.connect(eng + "/Abyssal_Blade").get();

                    boolean findInfo = false;
                    boolean findTips = false;
                    boolean findAbilities = false;
                    boolean findTrivia = false;
                    for (Element elementItem : docItemDescription.getElementById("mw-content-text").child(0).children()) {

                        if (elementItem.tag().toString().equals("h2")) {
                            findAbilities = false;
                        }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Additional_information") ||
                                    elementItem.child(1).attr("id").equals(".D0.94.D0.BE.D0.BF.D0.BE.D0.BB.D0.BD.D0.B8.D1.82.D0.B5.D0.BB.D1.8C.D0.BD.D0.B0.D1.8F_.D0.B8.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F") ||
                                    elementItem.child(1).attr("id").equals(".E9.A2.9D.E5.A4.96.E4.BF.A1.E6.81.AF")) {
                                findInfo = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Trivia") ||
                                    elementItem.child(1).attr("id").equals(".E8.8A.B1.E7.B5.AE") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D0.BD.D1.82.D0.B5.D1.80.D0.B5.D1.81.D0.BD.D1.8B.D0.B5_.D1.84.D0.B0.D0.BA.D1.82.D1.8B")) {
                                findTrivia = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Tips") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F")) {
                                findTips = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Ability") ||
                                    elementItem.child(0).attr("id").equals("Abilities") ||
                                    elementItem.child(1).attr("id").equals(".E6.8A.80.E8.83.BD") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D1.81.D0.BF.D0.BE.D0.BB.D1.8C.D0.B7.D0.BE.D0.B2.D0.B0.D0.BD.D0.B8.D0.B5") ||
                                    elementItem.child(1).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D0.B8") ||
                                    elementItem.child(1).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D1.8C")) {
                                findAbilities = true;
                                continue;
                            }
                        if (findTrivia) {
                            JSONArray trivia = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    trivia.add(element.text());
                            }
                            jsonObjectItem.put("trivia", trivia);
                            findTrivia = false;
                        }
                        if (elementItem.tag().toString().equals("div") && findAbilities) {
                            JSONObject jsonObjectAbility = new JSONObject();
                            for (Element element : elementItem.child(0).getElementsByTag("div")) {
                                if (element.attr("style").equals("font-weight: bold; font-cost: 110%; border-bottom: 1px solid black; background-color: #227722; color: white; padding: 3px 5px;")) {
                                    jsonObjectAbility.put("name", element.childNode(0).toString().substring(1));
                                }
                                if (element.attr("style").equals("")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element element1 : element.getElementsByTag("div")) {
                                        if (element1.attr("style").equals("display: inline-block; width: 32%; vertical-align: top;"))
                                            if (element1.text().length() > 0)
                                                jsonArrayDescription.add(element1.text());
                                        if (element1.attr("style").equals("vertical-align: top; padding: 3px 5px; border-top: 1px solid black;"))
                                            jsonObjectAbility.put("description", element1.text().trim());
                                    }
                                    if (jsonArrayDescription.size() > 0)
                                        jsonObjectAbility.put("elements", jsonArrayDescription);
                                }
                                if (element.attr("style").equals("vertical-align:top; padding: 3px 5px;")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element descript : element.children()) {
                                        if (descript.attr("style").equals(""))
                                            jsonArrayDescription.add(descript.text());
                                    }
                                    jsonObjectAbility.put("effects", jsonArrayDescription);
                                }
                            }
                            if (elementItem.children().size() > 1) {
                                JSONArray jsonArrayNotes = new JSONArray();
                                for (Element element : elementItem.child(1).children()) {
                                    if (element.tag().toString().equals("ul"))
                                        jsonArrayNotes.add(element.text());
                                }
                                if (jsonArrayNotes.size() > 0)
                                    jsonObjectAbility.put("notes", jsonArrayNotes);
                            }
                            if (jsonObjectAbility.size() > 0)
                                jsonArrayAbilities.add(jsonObjectAbility);

                            if (jsonArrayAbilities.size() > 0)
                                jsonObjectItem.put("abilities", jsonArrayAbilities);
                        }
                        if (findTips) {
                            JSONArray tips = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    tips.add(element.text());
                            }
                            jsonObjectItem.put("tips", tips);
                            findTips = false;
                        }
                        if (findInfo) {
                            JSONArray items = new JSONArray();
                            for (Element element : elementItem.children()) {
                                items.add(element.text());
                            }
                            jsonObjectItem.put("info", items);
                            findInfo = false;
                        }
                        //нашли бокс
                        if (elementItem.tag().toString().equals("table") && elementItem.attr("class").equals("infobox")) {
                            jsonObjectSection.put("nameZh", docItemDescription.getElementById("firstHeading").text());
                            if (loadImage) {
                                String imageUrl = docItemDescription.getElementById("itemmainimage").child(0).child(0).attr("src");
                                image = ImageIO.read(new URL(imageUrl));
                                new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase()).mkdirs();
                                ImageIO.write(image, "png", new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\full.png"));
                            }
                            jsonObjectItem.put("description", elementItem.child(0).child(2).text());
                            jsonObjectItem.put("cost", docItemDescription.getElementsByAttributeValue("style", "display:flex; align-items:center; background-color:#B44335; color:white;").get(0).children().get(0).text().split(" ")[1]);
                            JSONArray attrs = new JSONArray();
                            boolean findRecipe = false;
                            for (int i = 0; i < elementItem.getElementsByTag("tbody").get(1).children().size(); i++) {
                                Element attr = elementItem.getElementsByTag("tbody").get(1).child(i);
                                String string = attr.text();
                                if (string.length() > 0 && i != elementItem.getElementsByTag("tbody").get(1).children().size() - 2)
                                    attrs.add(string);
                                if (i != elementItem.getElementsByTag("tbody").get(1).children().size() - 1) {
                                    findRecipe = true;
                                    continue;
                                }
                                if (findRecipe) {
                                    if (attr.child(0).children().size() == 3) {
                                        JSONArray recipe = new JSONArray();
                                        for (Element element : attr.child(0).getElementsByAttributeValue("style", "display:table-cell;")) {
                                            recipe.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("further", recipe);
                                    }
                                    if (attr.child(0).children().size() == 5) {
                                        JSONArray recipeFurther = new JSONArray();
                                        JSONArray recipeContains = new JSONArray();
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(0).children()) {
                                            recipeFurther.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(1).children()) {
                                            recipeContains.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("contains", recipeContains);
                                        jsonObjectItem.put("further", recipeFurther);
                                    }
                                    if (attr.child(0).children().size() == 1) {
//                                        jsonObjectItem.put("further",attr.getElementsByTag("a").attr("href") );
                                    }
                                    findRecipe = false;
                                }
                                jsonObjectItem.put("attrs", attrs);
                            }
//                            for (Element attr : elementItem.getElementsByTag("tbody").get(1).children()) {
//                                String string = attr.text();
//                                if (string.length() > 0 && !string.equals("Recipe"))
//                                    attrs.add(string);
//                                if (string.equals("Recipe")) {
//                                    findRecipe = true;
//                                    continue;
//                                }
//                                if (findRecipe) {
//                                    if (attr.child(0).children().cost() == 3) {
//                                        JSONArray recipe = new JSONArray();
//                                        for (Element element : attr.child(0).getElementsByAttributeValue("style", "display:table-cell;")) {
//                                            recipe.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        jsonObjectItem.put("further", recipe);
//                                    }
//                                    if (attr.child(0).children().cost() == 5) {
//                                        JSONArray recipeFurther = new JSONArray();
//                                        JSONArray recipeContains = new JSONArray();
//                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(0).children()) {
//                                            recipeFurther.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(1).children()) {
//                                            recipeContains.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        jsonObjectItem.put("contains", recipeContains);
//                                        jsonObjectItem.put("further", recipeFurther);
//                                    }
//                                    if (attr.child(0).children().cost() == 1) {
////                                        jsonObjectItem.put("further",attr.getElementsByTag("a").attr("href") );
//                                    }
//                                    findRecipe = false;
//                                }
//                                jsonObjectItem.put("attrs", attrs);
//                            }
                        }
                    }
                    try {
                        FileWriter file = new FileWriter("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\zh_description.json");
                        file.write(jsonObjectItem.toJSONString());
                        file.flush();
                        file.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    jsonArrayItems.add(jsonObjectSection);
                    System.out.println("loaded" + itemName);
                }
            }

//            try {
//                FileWriter file = new FileWriter("C:\\" + "items.json");
//                file.write(jsonArrayItems.toJSONString());
//                file.flush();
//                file.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(itemName);
        }
    }

    private static void loadRusItems(boolean loadImage) {
        BufferedImage image = null;
        String itemName = "";
        try {
            String ru = "https://dota2-ru.gamepedia.com";
            Document docRus = Jsoup.connect("https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B").get();

            int count = 0;
            JSONArray jsonArrayItems = new JSONArray();
            JSONObject jsonObjectSection;


            for (Element itemGroup : docRus.getElementsByClass("itemlist")) {
                count++;
                if (count >= 13) break;
                for (Element item : itemGroup.children()) {
                    itemName = item.child(0).attr("href");

                    if (itemName.startsWith("/Animal_Courier")) continue;
                    if (itemName.startsWith("/Power_Treads")) continue;

                    String itemNameImage = itemName.replaceAll("\\(.+\\)", "").trim().replace(" ", "_").replace("\\\\", "");
                    if (loadImage) {
                        String imageUrl = item.child(0).child(0).attr("src");
                        image = ImageIO.read(new URL(imageUrl));
                        new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase()).mkdirs();
                        ImageIO.write(image, "png", new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\full.png"));
                    }
                    jsonObjectSection = new JSONObject();
                    jsonObjectSection.put("name", itemNameImage.toLowerCase().substring(1));
                    jsonObjectSection.put("nameEng", itemNameImage.substring(1).replace("_", " ").trim());
//                    jsonObjectSection.put("href", itemName);

                    JSONObject jsonObjectItem = new JSONObject();

                    JSONArray jsonArrayAbilities = new JSONArray();//ряды для способностей
                    Document docItemDescription = Jsoup.connect(ru + "/" + jsonObjectSection.get("nameEng").toString().replace(" ", "_")).get();
//                    Document docItemDescription = Jsoup.connect(eng + "/Abyssal_Blade").get();

                    boolean findInfo = false;
                    boolean findTips = false;
                    boolean findAbilities = false;
                    boolean findTrivia = false;
                    for (Element elementItem : docItemDescription.getElementById("mw-content-text").child(0).children()) {

//                        if (elementItem.children().cost() <= 1) continue;
                        if (elementItem.tag().toString().equals("h2")) {
                            findAbilities = false;
                        }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Additional_information") ||
                                    elementItem.child(1).attr("id").equals(".D0.94.D0.BE.D0.BF.D0.BE.D0.BB.D0.BD.D0.B8.D1.82.D0.B5.D0.BB.D1.8C.D0.BD.D0.B0.D1.8F_.D0.B8.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F")) {
                                findInfo = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Trivia") ||
                                    elementItem.child(1).attr("id").equals(".E8.8A.B1.E7.B5.AE") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D0.BD.D1.82.D0.B5.D1.80.D0.B5.D1.81.D0.BD.D1.8B.D0.B5_.D1.84.D0.B0.D0.BA.D1.82.D1.8B")) {
                                findTrivia = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Tips") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F")) {
                                findTips = true;
                                continue;
                            }
                        if (elementItem.children().size() > 1)
                            if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Ability") ||
                                    elementItem.child(0).attr("id").equals("Abilities") ||
                                    elementItem.child(1).attr("id").equals(".E6.8A.80.E8.83.BD") ||
                                    elementItem.child(1).attr("id").equals(".D0.98.D1.81.D0.BF.D0.BE.D0.BB.D1.8C.D0.B7.D0.BE.D0.B2.D0.B0.D0.BD.D0.B8.D0.B5") ||
                                    elementItem.child(1).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D0.B8") ||
                                    elementItem.child(1).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D1.8C")) {
                                findAbilities = true;
                                continue;
                            }
                        if (findTrivia) {
                            JSONArray trivia = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    trivia.add(element.text());
                            }
                            jsonObjectItem.put("trivia", trivia);
                            findTrivia = false;
                        }
                        if (elementItem.tag().toString().equals("div") && findAbilities) {
                            JSONObject jsonObjectAbility = new JSONObject();
                            for (Element element : elementItem.child(0).getElementsByTag("div")) {
                                if (element.attr("style").equals("font-weight: bold; font-size: 110%; border-bottom: 1px solid black; background-color: #227722; color: white; padding: 3px 5px;")) {
                                    jsonObjectAbility.put("name", element.childNode(0).toString().substring(1));
                                }
                                if (element.attr("style").equals("")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element element1 : element.getElementsByTag("div")) {
                                        if (element1.attr("style").equals("display: inline-block; width: 32%; vertical-align: top;"))
                                            if (element1.text().length() > 0)
                                                jsonArrayDescription.add(element1.text());
                                        if (element1.attr("style").equals("vertical-align: top; padding: 3px 5px; border-top: 1px solid black;"))
                                            jsonObjectAbility.put("description", element1.text().trim());
                                    }
                                    if (jsonArrayDescription.size() > 0)
                                        jsonObjectAbility.put("elements", jsonArrayDescription);
                                }
                                if (element.attr("style").equals("vertical-align:top; padding: 3px 5px;")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element descript : element.children()) {
                                        if (descript.attr("style").equals(""))
                                            jsonArrayDescription.add(descript.text());
                                    }
                                    jsonObjectAbility.put("effects", jsonArrayDescription);
                                }
                            }
                            if (elementItem.children().size() > 1) {
                                JSONArray jsonArrayNotes = new JSONArray();
                                for (Element element : elementItem.child(1).children()) {
                                    if (element.tag().toString().equals("ul"))
                                        jsonArrayNotes.add(element.text());
                                }
                                if (jsonArrayNotes.size() > 0)
                                    jsonObjectAbility.put("notes", jsonArrayNotes);
                            }
                            if (jsonObjectAbility.size() > 0)
                                jsonArrayAbilities.add(jsonObjectAbility);

                            if (jsonArrayAbilities.size() > 0)
                                jsonObjectItem.put("abilities", jsonArrayAbilities);
                        }
                        if (findTips) {
                            JSONArray tips = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    tips.add(element.text());
                            }
                            jsonObjectItem.put("tips", tips);
                            findTips = false;
                        }
                        if (findInfo) {
                            JSONArray items = new JSONArray();
                            for (Element element : elementItem.children()) {
                                items.add(element.text());
                            }
                            jsonObjectItem.put("info", items);
                            findInfo = false;
                        }
                        //нашли бокс
                        if (elementItem.tag().toString().equals("table") && elementItem.attr("class").equals("infobox")) {
//                            jsonObjectSection.put("nameZh", docItemDescription.getElementById("firstHeading").text());
                            jsonObjectItem.put("description", elementItem.child(0).child(2).text());
                            jsonObjectItem.put("cost", docItemDescription.getElementsByAttributeValue("style", "display:flex; align-items:center; background-color:#B44335; color:white;").get(0).children().get(0).text().split(" ")[1]);
                            JSONArray attrs = new JSONArray();
                            boolean findRecipe = false;
                            for (int i = 0; i < elementItem.getElementsByTag("tbody").get(1).children().size(); i++) {
                                Element attr = elementItem.getElementsByTag("tbody").get(1).child(i);
                                String string = attr.text();
                                if (string.length() > 0 && elementItem.getElementsByTag("tbody").get(1).children().size() != i + 2)
                                    attrs.add(string);
                                if (elementItem.getElementsByTag("tbody").get(1).children().size() == i + 2) {
                                    findRecipe = true;
                                    continue;
                                }
                                if (findRecipe) {
                                    if (attr.child(0).children().size() == 3) {
                                        JSONArray recipe = new JSONArray();
                                        for (Element element : attr.child(0).getElementsByAttributeValue("style", "display:table-cell;")) {
                                            recipe.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("further", recipe);
                                    }
                                    if (attr.child(0).children().size() == 5) {
                                        JSONArray recipeFurther = new JSONArray();
                                        JSONArray recipeContains = new JSONArray();
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(0).children()) {
                                            recipeFurther.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(1).children()) {
                                            recipeContains.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("contains", recipeContains);
                                        jsonObjectItem.put("further", recipeFurther);
                                    }
                                    if (attr.child(0).children().size() == 1) {
//                                        jsonObjectItem.put("further",attr.getElementsByTag("a").attr("href") );
                                    }
                                    findRecipe = false;
                                }
                                jsonObjectItem.put("attrs", attrs);
                            }
//                            for (Element attr : elementItem.getElementsByTag("tbody").get(1).children()) {
//                                String string = attr.text();
//                                if (string.length() > 0 && !string.equals("Recipe"))//todo русскую залупу прочесть надо
//                                    attrs.add(string);
//                                if (string.equals("Recipe")) {
//                                    findRecipe = true;
//                                    continue;
//                                }
//                                if (findRecipe) {
//                                    if (attr.child(0).children().cost() == 3) {
//                                        JSONArray recipe = new JSONArray();
//                                        for (Element element : attr.child(0).getElementsByAttributeValue("style", "display:table-cell;")) {
//                                            recipe.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        jsonObjectItem.put("further", recipe);
//                                    }
//                                    if (attr.child(0).children().cost() == 5) {
//                                        JSONArray recipeFurther = new JSONArray();
//                                        JSONArray recipeContains = new JSONArray();
//                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(0).children()) {
//                                            recipeFurther.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(1).children()) {
//                                            recipeContains.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
//                                        }
//                                        jsonObjectItem.put("contains", recipeContains);
//                                        jsonObjectItem.put("further", recipeFurther);
//                                    }
//                                    if (attr.child(0).children().cost() == 1) {
////                                        jsonObjectItem.put("further",attr.getElementsByTag("a").attr("href") );
//                                    }
//                                    findRecipe = false;
//                                }
//                                jsonObjectItem.put("attrs", attrs);
//                            }
                        }
                    }
                    try {
                        FileWriter file = null;
                        if (itemNameImage.toLowerCase().endsWith("_aghanim%27s_scepter")) {
                            file = new FileWriter("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + "aghanim%27s_blessing" + "\\ru_description.json");
                        } else {
                            file = new FileWriter("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\ru_description.json");
                        }
                        file.write(jsonObjectItem.toJSONString());
                        file.flush();
                        file.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    jsonArrayItems.add(jsonObjectSection);
                    System.out.println("loaded ru-" + itemName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(itemName);
        }
    }

    private static void loadEngItems(boolean loadImage) {
        BufferedImage image = null;
        String itemName = "";
        try {
            String eng = "https://dota2.gamepedia.com";
            Document docRus = Jsoup.connect("https://dota2.gamepedia.com/Items").get();

            int count = 0;
            JSONArray jsonArrayItems = new JSONArray();
            JSONObject jsonObjectSection;


            for (Element itemGroup : docRus.getElementsByClass("itemlist")) {
                count++;
                if (count >= 13) break;
                for (Element item : itemGroup.children()) {
                    itemName = item.child(0).attr("href");

                    if (itemName.startsWith("/Animal_Courier")) continue;
                    if (itemName.startsWith("/Power_Treads")) continue;


                    String itemNameImage = itemName.replaceAll("\\(.+\\)", "").trim().replace(" ", "_").replace("\\\\", "");
                    if (loadImage) {
                        String imageUrl = item.child(0).child(0).attr("src");
                        image = ImageIO.read(new URL(imageUrl));
                        new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase()).mkdirs();
                        ImageIO.write(image, "png", new File("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\full.png"));
                    }
                    jsonObjectSection = new JSONObject();
                    jsonObjectSection.put("name", itemNameImage.toLowerCase().substring(1));
                    jsonObjectSection.put("nameEng", itemNameImage.substring(1).replace("_", " ").trim());
//                    jsonObjectSection.put("href", itemName);

                    JSONObject jsonObjectItem = new JSONObject();

                    JSONArray jsonArrayAbilities = new JSONArray();//ряды для способностей
                    Document docItemDescription = Jsoup.connect(eng + "/" + jsonObjectSection.get("nameEng").toString().replace(" ", "_")).get();
//                    Document docItemDescription = Jsoup.connect(eng + "/Abyssal_Blade").get();

                    boolean findInfo = false;
                    boolean findTips = false;
                    boolean findAbilities = false;
                    boolean findTrivia = false;
                    for (Element elementItem : docItemDescription.getElementById("mw-content-text").child(0).children()) {

                        if (elementItem.children().size() == 0) continue;
                        if (elementItem.tag().toString().equals("h2")) {
                            findAbilities = false;
                        }
                        if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Additional_information") || elementItem.child(0).attr("id").equals(".D0.94.D0.BE.D0.BF.D0.BE.D0.BB.D0.BD.D0.B8.D1.82.D0.B5.D0.BB.D1.8C.D0.BD.D0.B0.D1.8F_.D0.B8.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F")) {
                            findInfo = true;
                            continue;
                        }
                        if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Trivia") || elementItem.child(0).attr("id").equals(".E8.8A.B1.E7.B5.AE") || elementItem.child(0).attr("id").equals(".D0.98.D0.BD.D1.82.D0.B5.D1.80.D0.B5.D1.81.D0.BD.D1.8B.D0.B5_.D1.84.D0.B0.D0.BA.D1.82.D1.8B")) {
                            findTrivia = true;
                            continue;
                        }
                        if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Tips") || elementItem.child(0).attr("id").equals(".D0.98.D0.BD.D1.84.D0.BE.D1.80.D0.BC.D0.B0.D1.86.D0.B8.D1.8F")) {
                            findTips = true;
                            continue;
                        }
                        if (elementItem.tag().toString().equals("h2") && elementItem.child(0).attr("id").equals("Ability") || elementItem.child(0).attr("id").equals(".E6.8A.80.E8.83.BD") || elementItem.child(0).attr("id").equals("Abilities") || elementItem.child(0).attr("id").equals(".D0.98.D1.81.D0.BF.D0.BE.D0.BB.D1.8C.D0.B7.D0.BE.D0.B2.D0.B0.D0.BD.D0.B8.D0.B5") || elementItem.child(0).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D0.B8") || elementItem.child(0).attr("id").equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D1.8C")) {
                            findAbilities = true;
                            continue;
                        }
                        if (findTrivia) {
                            JSONArray trivia = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    trivia.add(element.text());
                            }
                            jsonObjectItem.put("trivia", trivia);
                            findTrivia = false;
                        }
                        if (elementItem.tag().toString().equals("div") && findAbilities) {
                            JSONObject jsonObjectAbility = new JSONObject();
                            for (Element element : elementItem.child(0).getElementsByTag("div")) {
                                if (element.attr("style").equals("font-weight: bold; font-cost: 110%; border-bottom: 1px solid black; background-color: #227722; color: white; padding: 3px 5px;")) {
                                    jsonObjectAbility.put("name", element.childNode(0).toString().substring(1));
                                }
                                if (element.attr("style").equals("")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element element1 : element.getElementsByTag("div")) {
                                        if (element1.attr("style").equals("display: inline-block; width: 32%; vertical-align: top;"))
                                            if (element1.text().length() > 0)
                                                jsonArrayDescription.add(element1.text());
                                        if (element1.attr("style").equals("vertical-align: top; padding: 3px 5px; border-top: 1px solid black;"))
                                            jsonObjectAbility.put("description", element1.text().trim());
                                    }
                                    if (jsonArrayDescription.size() > 0)
                                        jsonObjectAbility.put("elements", jsonArrayDescription);
                                }
                                if (element.attr("style").equals("vertical-align:top; padding: 3px 5px;")) {
                                    JSONArray jsonArrayDescription = new JSONArray();
                                    for (Element descript : element.children()) {
                                        if (descript.attr("style").equals(""))
                                            jsonArrayDescription.add(descript.text());
                                    }
                                    jsonObjectAbility.put("effects", jsonArrayDescription);
                                }
                            }
                            if (elementItem.children().size() > 1) {
                                JSONArray jsonArrayNotes = new JSONArray();
                                for (Element element : elementItem.child(1).children()) {
                                    if (element.tag().toString().equals("ul"))
                                        jsonArrayNotes.add(element.text());
                                }
                                if (jsonArrayNotes.size() > 0)
                                    jsonObjectAbility.put("notes", jsonArrayNotes);
                            }
                            if (jsonObjectAbility.size() > 0)
                                jsonArrayAbilities.add(jsonObjectAbility);

                            if (jsonArrayAbilities.size() > 0)
                                jsonObjectItem.put("abilities", jsonArrayAbilities);
                        }
                        if (findTips) {
                            JSONArray tips = new JSONArray();
                            for (Element element : elementItem.children()) {
                                if (element.text().length() > 0)
                                    tips.add(element.text());
                            }
                            jsonObjectItem.put("tips", tips);
                            findTips = false;
                        }
                        if (findInfo) {
                            JSONArray items = new JSONArray();
                            for (Element element : elementItem.children()) {
                                items.add(element.text());
                            }
                            jsonObjectItem.put("info", items);
                            findInfo = false;
                        }
                        //нашли бокс
                        if (elementItem.tag().toString().equals("table") && elementItem.attr("class").equals("infobox")) {
                            jsonObjectSection.put("nameZh", docItemDescription.getElementById("firstHeading").text());
                            jsonObjectItem.put("description", elementItem.child(0).child(2).text());
                            jsonObjectItem.put("cost", docItemDescription.getElementsByAttributeValue("style", "display:flex; align-items:center; background-color:#B44335; color:white;").get(0).children().get(0).text().split(" ")[1]);
                            JSONArray attrs = new JSONArray();
                            boolean findRecipe = false;
                            for (int i = 0; i < elementItem.getElementsByTag("tbody").get(1).children().size(); i++) {
                                Element attr = elementItem.getElementsByTag("tbody").get(1).child(i);
                                String string = attr.text();
                                if (string.length() > 0 && elementItem.getElementsByTag("tbody").get(1).children().size() != i + 2)
                                    attrs.add(string);
                                if (elementItem.getElementsByTag("tbody").get(1).children().size() == i + 2) {
                                    findRecipe = true;
                                    continue;
                                }
                                if (findRecipe) {
                                    if (attr.child(0).children().size() == 3) {
                                        JSONArray recipe = new JSONArray();
                                        for (Element element : attr.child(0).getElementsByAttributeValue("style", "display:table-cell;")) {
                                            recipe.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("further", recipe);
                                    }
                                    if (attr.child(0).children().size() == 5) {
                                        JSONArray recipeFurther = new JSONArray();
                                        JSONArray recipeContains = new JSONArray();
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(0).children()) {
                                            recipeFurther.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        for (Element element : attr.getElementsByAttributeValue("style", "display:table-row;").get(1).children()) {
                                            recipeContains.add(element.child(0).attr("title").replaceAll("\\(.+\\)", "").trim().replace(" ", "_"));
                                        }
                                        jsonObjectItem.put("contains", recipeContains);
                                        jsonObjectItem.put("further", recipeFurther);
                                    }
                                    if (attr.child(0).children().size() == 1) {
//                                        jsonObjectItem.put("further",attr.getElementsByTag("a").attr("href") );
                                    }
                                    findRecipe = false;
                                }
                                jsonObjectItem.put("attrs", attrs);
                            }
                        }
                    }
                    try {
                        FileWriter file = new FileWriter("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\items\\" + itemNameImage.toLowerCase() + "\\eng_description.json");
                        file.write(jsonObjectItem.toJSONString());
                        file.flush();
                        file.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    jsonArrayItems.add(jsonObjectSection);
                    System.out.println("loaded eng-" + itemName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(itemName);
        }
    }

    private static List<String> loadHeroesFromFileZh() {
        List<String> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray object = (JSONArray) parser.parse(new FileReader("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\heroes.json"));
            for (Object o : object) {
                String hero = (String) ((JSONObject) o).get("nameZh");
                if (hero.contains("_") & hero.split("_").length == 2)
                    hero = hero.split("_")[0] + "_" + hero.split("_")[1].substring(0, 1).toUpperCase() + hero.split("_")[1].substring(1);
                if (hero.equals("keeper_of_the_light")) hero = "keeper_of_the_Light";
                if (hero.equals("queen_of_pain")) hero = "queen_of_Pain";
                if (hero.equals("anti-mage")) hero = "anti-Mage";
                list.add(hero);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<String> loadHeroesFromFile() {
        List<String> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONArray object = (JSONArray) parser.parse(new FileReader("C:\\Users\\i30mb1\\AndroidProjects\\AD2\\app\\src\\main\\assets\\heroes.json"));
            for (Object o : object) {
                String hero = (String) ((JSONObject) o).get("name");
                if (hero.contains("_") & hero.split("_").length == 2)
                    hero = hero.split("_")[0] + "_" + hero.split("_")[1].substring(0, 1).toUpperCase() + hero.split("_")[1].substring(1);
                if (hero.equals("keeper_of_the_light")) hero = "keeper_of_the_Light";
                if (hero.equals("queen_of_pain")) hero = "queen_of_Pain";
                if (hero.equals("anti-mage")) hero = "anti-Mage";
                list.add(hero);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void loadResponses(String heroName, String counter) {
        String URLS[] = new String[]{
                "https://dota2-ru.gamepedia.com/" + heroName + "/%D0%A0%D0%B5%D0%BF%D0%BB%D0%B8%D0%BA%D0%B8",
                "https://dota2.gamepedia.com/" + heroName + "/Responses"
        };
        String language = "eng";
        int count = 0;
        for (String URL : URLS) {
            switch (count) {
                case 0:
                    language = "ru";
                    break;
                case 1:
                    language = "eng";
                    break;
            }
            count++;
//            language = (language.equals("eng") ? "ru" : "eng");
            Document doc = null;
            try {
                int countResponses = 0;
                doc = Jsoup.connect(URL).get();
                Elements children = doc.getElementById("mw-content-text").child(0).children();
                JSONArray jsonArrayHeroes = new JSONArray();//cоздаём ряд [...] для всех секций
                JSONObject jsonObjectSection = new JSONObject();//создаём обьект {...} для секции
                JSONArray jsonArrayResponsesForSection = new JSONArray();//создаём ряды [...] для обьектов с репликами
                JSONObject jsonObjectResponse = new JSONObject();//создаём обьект {...} для одной реплики
                for (Element child : children) {
                    if (child.tag().toString().equals("h2")) { //нашли имя секции, значит создаём обьект
                        if (jsonObjectSection.size() == 2 & jsonObjectResponse.size() != 0)
                            jsonArrayHeroes.add(jsonObjectSection);//перед созданием новой секции сохраняем старую
                        jsonObjectSection = new JSONObject();
                        jsonArrayResponsesForSection = new JSONArray();
                        countResponses = countResponses + 1;
                        if (child.children().size() > 1) {
                            jsonObjectSection.put("name", child.child(1).text().trim());
                        } else {
                            jsonObjectSection.put("name", child.child(0).text().trim());
                        }
                    }
                    if (child.tag().toString().equals("ul")) { //нашли реплики для секции
                        jsonObjectResponse = new JSONObject();
                        for (Element response : child.children()) {
                            if (response.children().size() == 0)
                                continue;
                            if (response.child(0).children().size() == 0)
                                continue;//бывают реплики без URL их незаписываем

                            Elements audios = response.getElementsByTag("audio");
                            boolean firstTime = true;
                            boolean isArcane = false;
                            String lastHref = "";
                            for (Element audio : audios) {
                                jsonObjectResponse = new JSONObject();//response.childNode(0).toString().trim()
                                StringBuilder stringIcons = new StringBuilder();
                                String href = audio.getElementsByTag("audio").get(0).child(0).attr("src");
                                if(lastHref.equals(href)) break;
                                jsonObjectResponse.put("href", href);
                                String title;
                                if (firstTime) {
                                    firstTime = false;
                                    title = response.childNode(response.childNodes().size() - 1).toString().trim();
                                } else {
                                    title = response.childNode(response.childNodes().size() - 1).toString().trim();
//                                    if (href.contains("_arc_") || href.contains("_dem_") || href.contains("_bearform_") || href.contains("_big_")||href.contains("_dragon_")) {
                                    if (true) {
                                        stringIcons.append(heroName.toLowerCase()).append("_arcane+");
                                        isArcane = true;
                                    }
//                                    title += "( arcane )";
                                }
                                jsonObjectResponse.put("title", title);
                                if (response.childNode(response.childNodes().size() - 1).toString().trim().startsWith("<"))
                                    jsonObjectResponse.put("title", heroName + "_" + countResponses);//имя для реплики без имени
                                countResponses = countResponses + 1;
                                if (response.children().size() > 2) {
                                    for (Element icon : response.children()) {
                                        if (icon.attr("href").startsWith("/"))
                                            stringIcons.append(icon.attr("href").replace("/", "").toLowerCase()).append("+");
                                    }
                                }
                                if (stringIcons.length() > 0) {
                                    jsonObjectResponse.put("icon", stringIcons.toString());
                                }
//                                else if (isArcane) {
//                                    jsonObjectResponse.put("icon", heroName.toLowerCase() + "_arcane");
//                                }
                                lastHref = href;
                                jsonArrayResponsesForSection.add(jsonObjectResponse);//кладём реплику в секцию для реплик
                            }
                        }
                        jsonObjectSection.put("responses", jsonArrayResponsesForSection);//кладём всю секцию
                    }
                }
                jsonArrayHeroes.add(jsonObjectSection);//кладём последнюю секцию с репликами

                try {
                    FileWriter file = new FileWriter(System.getProperty("user.dir") + "\\app\\src\\main\\assets\\heroes\\" + heroName.toLowerCase() + "\\" + language + "_responses.json");
                    file.write(jsonArrayHeroes.toJSONString());
                    file.flush();
                    file.close();
                    if (language.equals("ru")) {
                        System.out.print("hero" + counter + "response saved " + language + "." + heroName + " || ");
                    } else {
                        System.out.println(language + "." + heroName);
                    }
                } catch (IOException ex) {
                    System.out.println("hero response not saved " + language + "." + heroName);
                }
            } catch (IOException e) {
                System.out.println("error: " + URL);
            }
        }
    }

    private static void loadHeroesNames(String[] heroes) {

        double str, str_, agi, agi_, inte, inte_, hp, mp;
        Document doc;
        JSONArray jsonArray = new JSONArray();
        String herrr = "";
        try {

            for (String hero : heroes) {
                JSONObject oneHero = new JSONObject();
                if (hero.equals("Skywrath_Mage")) herrr = hero;
                else herrr = hero.toLowerCase();
                doc = Jsoup.connect("http://www.dota2.com/hero/" + herrr.replace(" ", "_") + "/").get();
                String LocalizedName = doc.body().getElementsByTag("h1").text();
                String intelString = doc.body().getElementById("overview_IntVal").text();
                String[] partsIntel = intelString.split(" ");
                inte = Double.parseDouble(partsIntel[0]);
                inte_ = Double.parseDouble(partsIntel[2]);
                String strString = doc.body().getElementById("overview_StrVal").text();
                String[] partsString = strString.split(" ");
                str = Double.parseDouble(partsString[0]);
                str_ = Double.parseDouble(partsString[2]);
                String agiString = doc.body().getElementById("overview_AgiVal").text();
                String[] partsAgi = agiString.split(" ");
                agi = Double.parseDouble(partsAgi[0]);
                agi_ = Double.parseDouble(partsAgi[2]);
                String[] mp_hp = doc.body().getElementsByClass("statRowColW").text().split(" ");
                hp = Double.parseDouble(mp_hp[2]);
                mp = Double.parseDouble(mp_hp[5]);

//                oneHero.put("name", herrr);
//                oneHero.put("strength", str);
//                oneHero.put("strengthInc", str_);
//                oneHero.put("agility", agi_);
//                oneHero.put("agilityInc", agi);
//                oneHero.put("intellect", inte);
//                oneHero.put("intellectInc", inte_);
//                oneHero.put("mp", mp);
//                oneHero.put("hp", hp);
                oneHero.put("name", LocalizedName.replace(" ", "_").replace("-", "").toLowerCase());
                oneHero.put("nameEng", LocalizedName);
                doc = Jsoup.connect("http://www.dota2.com/hero/" + herrr.replace(" ", "_") + "/?l=schinese").get();
                oneHero.put("nameZh", doc.body().getElementsByTag("h1").text());
                jsonArray.add(oneHero);
                System.out.println("hero add's \"" + herrr + "\"");
            }
        } catch (NullPointerException e) {
            System.out.println("cant load stats for hero \"" + herrr + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter file = new FileWriter(System.getProperty("user.dir") + "\\app\\src\\main\\assets\\" + "heroes.json");
            file.write(jsonArray.toJSONString());
            file.flush();
            file.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadSpellsAndDescription(String heroName, String counter) {
        String URLS[] = new String[]{
                "https://dota2.gamepedia.com/" + heroName,
                "https://dota2-ru.gamepedia.com/" + heroName,
//                "https://dota2-zh.gamepedia.com/" + heroName,
        };
        for (String URL : URLS) {
            String language = "eng";
            if (URL.contains("-ru")) {
//                URL = "https://dota2-ru.gamepedia.com/%D0%A0%D0%BE%D1%88%D0%B0%D0%BD";
                language = "ru";
            }
            if (URL.contains("-zh")) language = "zh";

            Document doc = null;
            try {
                doc = Jsoup.connect(URL).get();

                JSONObject jsonObject = new JSONObject();//создаём обьект для героя
                JSONArray jsonArrayAbilities = new JSONArray();//ряды для способностей
                JSONObject jsonObjectAbility = new JSONObject();//обьект для одной способности
                jsonObject.put("desc", doc.getElementsByClass("mw-parser-output").get(1).child(2).text()); //кладём описание героя
                Elements children = doc.getElementById("mw-content-text").child(0).children();
                boolean findBio = false;
                boolean findAbilities = false;
                boolean findTrivia = false;
                boolean findTips = false;
                boolean findTalents = false;
                for (Element child : children) {
                    if (child.tag().toString().equals("h2")) { //нашли заголовок
                        findBio = false;
                        findTrivia = false;
                        if (findAbilities) {
                            jsonObject.put("abilities", jsonArrayAbilities);//кладём все способности в обьект
                            //todo только для рошана должна быть включенна и инвокера
                            findAbilities = heroName.toLowerCase().equals("invoker") || heroName.equals("祈求者");
                        }
                        if (child.child(1).id().equals(".D0.91.D0.B8.D0.BE.D0.B3.D1.80.D0.B0.D1.84.D0.B8.D1.8F") ||
                                child.child(0).id().equals("Bio") || child.child(1).id().equals(".E4.BB.8B.E7.BB.8D")) {//нашли биографию
                            findBio = true;
                            continue;
                        }
                        if (child.child(1).id().equals(".D0.A1.D0.BF.D0.BE.D1.81.D0.BE.D0.B1.D0.BD.D0.BE.D1.81.D1.82.D0.B8") ||
                                child.child(0).id().equals("Abilities") || child.child(1).id().equals(".E6.8A.80.E8.83.BD")) {//нашли Способности
                            findAbilities = true;
                            continue;
                        }
                        if (child.child(0).id().equals("Trivia") || child.child(1).id().equals(".D0.A4.D0.B0.D0.BA.D1.82.D1.8B") ||
                                child.child(1).id().equals(".E8.8A.B1.E7.B5.AE")) {
                            findTrivia = true;
                            continue;
                        }
                        if (child.child(1).id().equals(".D0.A1.D0.BE.D0.B2.D0.B5.D1.82.D1.8B")) {
                            findTips = true;
                            continue;
                        }
                        if (child.child(1).id().equals(".D0.A2.D0.B0.D0.BB.D0.B0.D0.BD.D1.82.D1.8B") ||
                                child.child(1).id().equals(".E5.A4.A9.E8.B5.8B") ||
                                child.child(0).id().equals("Talents")) {
                            findTalents = true;
                            findAbilities = false;
                            continue;
                        }

                    }
                    JSONArray jsonArrayTalents = new JSONArray();
                    JSONArray jsonArrayTalentsTips = new JSONArray();
                    if (child.tag().toString().equals("div") & findTalents) {
                        if (child.children().size() > 1) {
                            for (Element element : child.child(1).children()) {
                                if (element.tag().toString().equals("ul"))
                                    jsonArrayTalentsTips.add(element.text());
                            }
                            jsonObject.put("talentsTips", jsonArrayTalentsTips);
                        }

                        for (Element element : child.child(0).child(0).children()) {
                            if (element.tag().toString().equals("tr"))
                                if (element.children().size() > 1)
                                    jsonArrayTalents.add(element.child(0).text() + ":" + element.child(2).text());
                        }
                        jsonObject.put("talents", jsonArrayTalents);
                        findTalents = false;
                    }

                    if (child.tag().toString().equals("ul") & findTips) {
                        JSONArray jsonArrayTips = new JSONArray();
                        for (Element element : child.children()) {
                            jsonArrayTips.add(element.text().replaceAll("\\[[1-9]+]", ""));
                        }
                        jsonObject.put("tips", jsonArrayTips);
                        findTips = false;
                    }

                    if (child.tag().toString().equals("ul") & findTrivia) {
                        JSONArray jsonArrayTrivia = new JSONArray();
                        for (Element element : child.children()) {
                            Elements a = element.getElementsByClass("ext-audiobutton");
                            for (Element element1 : a) {
                                element1.remove();
                            }
                            jsonArrayTrivia.add(element.text().replaceAll("\\[[1-9]+]", "").replace(" Play ", " "));
                        }
                        jsonObject.put("trivia", jsonArrayTrivia);
                        findTrivia = false;
                    }

                    if (child.tag().toString().equals("div") & findBio) {//записываем биографию
                        jsonObject.put("bio", child.child(child.children().size() - 1).child(0).child(1).text());
                        findBio = false;
                    }

                    if (child.tag().toString().equals("div")
                            & findAbilities
                            & !child.attr("style").equals("font-style:italic;font-cost:15px;padding-left:2em;margin-bottom:0.5em")
                            & !child.attr("class").equals("thumb tright")
                            & !child.attr("style").equals("clear:both")
                            & !child.attr("style").equals("display:flex; flex-direction:column; align-items:center; text-align:center;")
                    ) {//нашли ветку со способностями
                        jsonObjectAbility = new JSONObject();//создаём обьект для способности

                        JSONArray jsonArrayEffects = new JSONArray();//ряд для эфектов
                        for (Element element : child.child(0).child(1).child(1).child(0).children()) {
                            if (element.text().length() != 0)
                                jsonArrayEffects.add(element.text());
                        }
                        jsonObjectAbility.put("effects", jsonArrayEffects);

                        jsonObjectAbility.put("spell", child.child(0).child(1).child(1).child(1).text());//описание для способности

                        jsonObjectAbility.put("cooldown", "0");
                        jsonObjectAbility.put("mana", "0");
                        JSONArray jsonArrayDescription = new JSONArray();//ряд для описания способностей
                        for (Element element : child.child(0).child(2).children()) {
                            if (element.attr("style").equals("")) {
                                jsonArrayDescription.add(element.text());
                            }
                            if (element.attr("style").equals("display:inline-block; margin:8px 0px 0px 50px; width:190px; vertical-align:top;")) {
                                jsonObjectAbility.put("cooldown", element.text());
                            }
                            if (element.attr("style").equals("display:inline-block; margin:8px 0px 0px 50px; width:370px; vertical-align:top;")) {
                                jsonObjectAbility.put("cooldown", element.text());
                            }
                            if (element.attr("style").equals("display:inline-block; margin:8px 0px 0px; width:190px; vertical-align:top;"))
                                jsonObjectAbility.put("mana", element.text());
                        }
                        jsonObjectAbility.put("description", jsonArrayDescription);


                        String name = child.child(0).child(0).childNode(0).toString();//имя способности
                        jsonObjectAbility.put("name", name.substring(1));
                        JSONArray jsonArrayElements = new JSONArray();//ряд для элементов
                        boolean findHotKey = true;
                        for (Element element : child.child(0).child(0).children().get(0).children()) {
                            if (element.tag().toString().equals("a")) {//свойство способности
                                jsonArrayElements.add(element.attr("title"));//бкб,сильвер и другие
                            }
//                            if (element.tag().toString().equals("span")) {//звук для способности
//                                jsonObjectAbility.put("sound", element.child(0).attr("href"));
//                            }
                            if (element.tag().toString().equals("div") & findHotKey) {//находим хот кей и сохраняем его
                                jsonObjectAbility.put("hot_key", element.text());
                                findHotKey = false;
                            }
                            if (element.tag().toString().equals("div")) {//находим легаси кей и сохраняем его
                                jsonObjectAbility.put("legacy_key", element.text());
                            }
                        }
                        jsonObjectAbility.put("elements", jsonArrayElements);//кладём все заметки для способности

                        //заметки
                        JSONArray jsonArrayNotes = new JSONArray();//ряд для заметок
                        if (child.children().size() == 2) {//проверяем есть ли заметки для спасобности
                            jsonArrayNotes = new JSONArray();//создаём ряд для заметок
                            for (Element note : child.child(1).children()) {//находим заметки
                                if (note.tag().toString().equals("ul"))
                                    jsonArrayNotes.add(note.text());
                            }
                        }
                        jsonObjectAbility.put("notes", jsonArrayNotes);//кладём все заметки для способности

                        jsonArrayAbilities.add(jsonObjectAbility);//кладём способность в ряд
                    }

                }
//                jsonObject.put("bio", doc.getElementsByAttributeValue("style", "display: table-cell; font-style: italic;").text());//кладём биографию героя
                try {
                    FileWriter file = new FileWriter(System.getProperty("user.dir") + "\\app\\src\\main\\assets\\heroes\\" + heroName.toLowerCase() + "\\" + language + "_abilities.json");
                    file.write(jsonObject.toJSONString());
                    file.flush();
                    file.close();
                    if (!URL.contains("-ru") || !URL.contains("-zh")) {
                        System.out.println("hero " + heroName + " saved " + language + counter + "(" + jsonObject.size() + ")");
                    }
                } catch (IOException ex) {
                    System.out.println("hero not saved " + URL + heroName);
                }
            } catch (IOException e) {
                System.out.println("error: " + URL);
            }
        }
    }

}
