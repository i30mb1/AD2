package n7.ad2.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.AppExecutors;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.activity.HeroFullActivity;
import n7.ad2.setting.SettingActivity;
import n7.ad2.heroes.db.HeroModel;
import n7.ad2.utils.Utils;
import n7.ad2.splash.SplashActivityViewModel;
import n7.ad2.worker.GuideWorker;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.activity.HeroFullActivity.HERO_NAME;
import static n7.ad2.worker.GuideWorker.HERO_CODE_NAME;

public class GuideFragment extends Fragment {

    LinearLayout ll_fragment_guide_further_items_create;
    private String heroFolder;
    private AppExecutors appExecutors;
    private View view;
    private TextView tv_fragment_guide_time;
    private TickerView tv_fragment_guide_lane;
    private LinearLayout ll_fragment_guide_best_versus;
    private LinearLayout ll_fragment_guide_skill_builds;
    private LinearLayout ll_fragment_guide_worst_versus;
    private LinearLayout ll_fragment_guide_starting_items;
    private LinearLayout ll_fragment_guide_further_items;
    private ProgressBar pb_fragment_guide;
    private int currentPage = 0;
    private int currentMenu = 1;
    private MenuItem previousMenu;
    private HeroModel hero;
    private boolean isPremium = false;
    private HashMap<String, String> hashMapSpells = new HashMap<>();
    private JSONObject jsonHeroDescription;
    private JSONArray jsonArrayHeroSpells;
    private LayoutInflater layoutInflater;
    private int maxItemsInRow = 0;

    public GuideFragment() {
        // Required empty public constructor
    }

    public static GuideFragment newInstance(String heroFolder, AppExecutors appExecutors) {
        GuideFragment guideFragment = new GuideFragment();
        guideFragment.appExecutors = appExecutors;
        guideFragment.heroFolder = heroFolder;
        return guideFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (isPremium) {
            inflater.inflate(R.menu.menu_fragment_guide_5_button, menu);
        } else {
            inflater.inflate(R.menu.menu_fragment_guide_2_button, menu);
        }
        previousMenu = menu.findItem(R.id.menu_fragment_guide_1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_guide_1:
                revertCurrentMenu();
                previousMenu = item;
                currentMenu = 1;
                item.setIcon(R.drawable.ic_menu_fragment_guide_1selected);
                currentPage = 0;
                loadPaged();
                break;
            case R.id.menu_fragment_guide_2:
                revertCurrentMenu();
                previousMenu = item;
                currentMenu = 2;
                item.setIcon(R.drawable.ic_menu_fragment_guide_2selected);
                currentPage = 1;
                loadPaged();
                break;
            case R.id.menu_fragment_guide_3:
                if (isPremium) {
                    revertCurrentMenu();
                    previousMenu = item;
                    currentMenu = 3;
                    item.setIcon(R.drawable.ic_menu_fragment_guide_3selected);
                    currentPage = 2;
                    loadPaged();
                } else {
                    showSnackBarPremium();
                }
                break;
            case R.id.menu_fragment_guide_4:
                if (isPremium) {
                    revertCurrentMenu();
                    previousMenu = item;
                    currentMenu = 4;
                    item.setIcon(R.drawable.ic_menu_fragment_guide_4selected);
                    currentPage = 3;
                    loadPaged();
                } else {
                    showSnackBarPremium();
                }
                break;
            case R.id.menu_fragment_guide_5:
                if (isPremium) {
                    revertCurrentMenu();
                    previousMenu = item;
                    currentMenu = 5;
                    item.setIcon(R.drawable.ic_menu_fragment_guide_5selected);
                    currentPage = 4;
                    loadPaged();
                } else {
                    showSnackBarPremium();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBarPremium() {
        Snackbar.make(view, R.string.all_only_for_subscribers, Snackbar.LENGTH_LONG).setAction(R.string.all_buy, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingActivity.class));
            }
        }).show();
    }

    private void revertCurrentMenu() {
        switch (currentMenu) {
            case 1:
                previousMenu.setIcon(R.drawable.ic_menu_fragment_guide_1);
                break;
            case 2:
                previousMenu.setIcon(R.drawable.ic_menu_fragment_guide_2);
                break;
            case 3:
                previousMenu.setIcon(R.drawable.ic_menu_fragment_guide_3);
                break;
            case 4:
                previousMenu.setIcon(R.drawable.ic_menu_fragment_guide_4);
                break;
            case 5:
                previousMenu.setIcon(R.drawable.ic_menu_fragment_guide_5);
                break;
        }
    }

    public int dpSize(int px) {
        if (getContext() != null) {
            float scale = getContext().getResources().getDisplayMetrics().density;
            return (int) (px * scale + 0.5f);
        }
        return 100;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guide, container, false);

        setHasOptionsMenu(true);
        SharedPreferences sp = MySharedPreferences.getSharedPreferences(getContext());
        isPremium = sp.getBoolean(PREMIUM, false);

        initViews();
        setDpMaxItemsInRow();
        loadHeroDescriptionFile();
        startGuideWork();
        layoutInflater = getLayoutInflater();

        SplashActivityViewModel heroesViewModel = ViewModelProviders.of(this).get(SplashActivityViewModel.class);
//        heroesViewModel.getHero(heroFolder).observe(this, new Observer<HeroModel>() {
//            @Override
//            public void onChanged(@Nullable HeroModel heroes) {
//                if (heroes != null) {
//                    hero = heroes;
//                    addBestVersusHeroes(hero.getBestVersus());
//                    addWorstVersusHeroes(hero.getWorstVersus());
//                    loadPaged();
//                }
//            }
//        });

        return view;
    }

    private void startGuideWork() {
        Data data = new Data.Builder().putString(HERO_CODE_NAME, heroFolder).build();
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GuideWorker.class).setInputData(data).build();
        WorkManager.getInstance().enqueue(worker);
        WorkManager.getInstance().getStatusById(worker.getId()).observe(this, new Observer<WorkStatus>() {
            @Override
            public void onChanged(@Nullable WorkStatus workStatus) {
                if (workStatus != null) {
                    if (workStatus.getState().isFinished()) {
                        pb_fragment_guide.setVisibility(View.GONE);
                    } else {
                        pb_fragment_guide.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void loadHeroDescriptionFile() {
        if (getContext() != null)
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        jsonHeroDescription = new JSONObject(Utils.readJSONFromAsset(getContext(), "heroes/" + heroFolder + "/eng_abilities.json"));
                        jsonArrayHeroSpells = (JSONArray) jsonHeroDescription.get("abilities");
                        hashMapSpells.clear();
                        for (int i = 0; i < jsonArrayHeroSpells.length(); i++) {
                            if (!hashMapSpells.containsKey(jsonArrayHeroSpells.getJSONObject(i).getString("name"))) {
                                if (jsonArrayHeroSpells.getJSONObject(i).getString("name").equals("Shadowraze (Near)")) {
                                    hashMapSpells.put("shadowraze", String.valueOf(i + 1).toLowerCase());
                                } else {
                                    hashMapSpells.put(jsonArrayHeroSpells.getJSONObject(i).getString("name").toLowerCase(), String.valueOf(i + 1));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    private void loadPaged() {
        try {
            if (hero != null) {
//                addStartingItems(hero.getStartingItems());
                addFurtherItems(hero.getFurtherItems());
                addSkillBuilds(hero.getSkillBuilds());
                addLane(hero.getLane());
                addTime(hero.getTime());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addTime(String times) {
        String timeArrays[] = times.split("\\+");
        tv_fragment_guide_time.setText(timeArrays[currentPage]);
    }

    private void addLane(String lanes) {
        String laneArrays[] = lanes.split("\\+");
        tv_fragment_guide_lane.setText(getString(R.string.fragment_guide_lane, laneArrays[currentPage]));
    }

    private void addSkillBuilds(final String skillBuilds) {
        ll_fragment_guide_skill_builds.removeAllViews();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String[] pages = skillBuilds.split("\\+");
                String[] spells = pages[currentPage].split("/");
                int countSpell = 1;
                for (final String spell : spells) {
                    final View item_list_hero = layoutInflater.inflate(R.layout.item_list_guide_hero, null);
                    final ImageView iv_item_image = item_list_hero.findViewById(R.id.iv);
                    final TextView tv_item_name = item_list_hero.findViewById(R.id.tv);
                    tv_item_name.setGravity(Gravity.END);
                    if (countSpell == 17) countSpell = 18;
                    if (countSpell == 19) countSpell = 20;
                    if (countSpell == 21) countSpell = 25;
                    tv_item_name.setText(String.valueOf(countSpell));
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ll_fragment_guide_skill_builds.addView(item_list_hero);
                            if (spell.equals("talent")) {
                                Picasso.get().load(R.drawable.hero_talent)
                                        .resize(dpSize(40), dpSize(40))
                                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                                        .into(iv_item_image);
                            } else {
                                Picasso.get().load("file:///android_asset/heroes/" + heroFolder + "/" + hashMapSpells.get(spell.toLowerCase()) + ".webp")
                                        .resize(dpSize(40), dpSize(40))
                                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                                        .into(iv_item_image);
                            }
                        }
                    });
                    countSpell++;
                }
            }
        });
    }

    private void addStartingItems(final String startingItems) {
        ll_fragment_guide_starting_items.removeAllViews();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String[] pages = startingItems.split("\\+");
                String[] items = pages[currentPage].split("/");
                int itemCount = 0;
                for (final String item : items) {
                    final View item_list_hero = layoutInflater.inflate(R.layout.item_list_guide_hero, null);
                    final ImageView iv_item_image = item_list_hero.findViewById(R.id.iv);
                    final TextView tv_item_name = item_list_hero.findViewById(R.id.tv);
                    tv_item_name.setVisibility(View.GONE);
                    if ((itemCount % maxItemsInRow) == 0) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ll_fragment_guide_further_items_create = new LinearLayout(getContext());
                                    ll_fragment_guide_further_items_create.setOrientation(LinearLayout.HORIZONTAL);
                                    ll_fragment_guide_further_items_create.setGravity(Gravity.CENTER);
                                    if (ll_fragment_guide_starting_items != null)
                                        ll_fragment_guide_starting_items.addView(ll_fragment_guide_further_items_create);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ll_fragment_guide_further_items_create.addView(item_list_hero);
                                Picasso.get().load("file:///android_asset/items/" + item + "/full.webp")
                                        .resize(dpSize(44), dpSize(32))
                                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                                        .into(iv_item_image);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    itemCount++;
                }
            }
        });
    }

    private void setDpMaxItemsInRow() {
        if (getActivity() != null) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            View item_list_hero = getLayoutInflater().inflate(R.layout.item_list_guide_hero, null);
            item_list_hero.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);

            maxItemsInRow = (int) Math.floor(dpWidth / item_list_hero.getMeasuredWidth() * displayMetrics.density);
        }
    }

    private void addFurtherItems(final String furtherItems) {
        ll_fragment_guide_further_items.removeAllViews();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String[] pages = furtherItems.split("\\+");
                String[] items = pages[currentPage].split("/");
                int itemCount = 0;
                for (final String item : items) {
                    final View item_list_hero = layoutInflater.inflate(R.layout.item_list_guide_hero, null);
                    final ImageView iv_item_image = item_list_hero.findViewById(R.id.iv);
                    final TextView tv_item_name = item_list_hero.findViewById(R.id.tv);
                    if (item.contains("^")) {
                        tv_item_name.setText(item.split("\\^")[1].trim());
                    } else {
                        tv_item_name.setVisibility(View.GONE);
                    }
//                    item_list_hero.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), ItemFullActivity.class);
//                            intent.putExtra(ITEM_CODE_NAME, item.split("\\^")[0]);
//                            intent.putExtra(ITEM_NAME, item.split("\\^")[0]);
//                            startActivity(intent);
//                        }
//                    });
                    if ((itemCount % maxItemsInRow) == 0) {
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                ll_fragment_guide_further_items_create = new LinearLayout(getContext());
                                ll_fragment_guide_further_items_create.setOrientation(LinearLayout.HORIZONTAL);
                                ll_fragment_guide_further_items_create.setGravity(Gravity.CENTER);
                                ll_fragment_guide_further_items.addView(ll_fragment_guide_further_items_create);
                            }
                        });
                    }
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ll_fragment_guide_further_items_create.addView(item_list_hero);
                            Picasso.get().load("file:///android_asset/items/" + item.split("\\^")[0] + "/full.webp")
                                    .resize(dpSize(44), dpSize(32))
                                    .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                                    .into(iv_item_image);
                        }
                    });
                    itemCount++;
                }
            }
        });

    }

    private void addBestVersusHeroes(final String bestVersusHeroes) {
        ll_fragment_guide_best_versus.removeAllViews();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String[] bestVersus = bestVersusHeroes.split("/");
                for (final String hero : bestVersus) {
                    final View item_list_hero = layoutInflater.inflate(R.layout.item_list_hero, null);
                    TextView tv_item_name = item_list_hero.findViewById(R.id.tv);
                    tv_item_name.setText(hero.split("\\^")[1]);
                    tv_item_name.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    final ImageView iv_item_image = item_list_hero.findViewById(R.id.iv);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        iv_item_image.setTransitionName("");
                    }
                    iv_item_image.setPadding(0, 0, 0, 0);
                    item_list_hero.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), HeroFullActivity.class);
                            intent.putExtra(HERO_NAME, hero.split("\\^")[0]);
                            intent.putExtra(HeroFullActivity.HERO_CODE_NAME, hero.split("\\^")[0]);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            ll_fragment_guide_best_versus.addView(item_list_hero);
                            Picasso.get().load("file:///android_asset/heroes/" + hero.split("\\^")[0] + "/full.webp")
                                    .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                    .into(iv_item_image);
                        }
                    });
                }
            }
        });
    }

    private void addWorstVersusHeroes(final String worstVersusHeroes) {
        ll_fragment_guide_worst_versus.removeAllViews();
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String[] bestVersus = worstVersusHeroes.split("/");
                    for (final String hero : bestVersus) {
                        final View item_list_hero = layoutInflater.inflate(R.layout.item_list_hero, null);
                        TextView tv_item_name = item_list_hero.findViewById(R.id.tv);
                        tv_item_name.setText(hero.split("\\^")[1]);
                        tv_item_name.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                        final ImageView iv_item_image = item_list_hero.findViewById(R.id.iv);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            iv_item_image.setTransitionName("");
                        }
                        iv_item_image.setPadding(0, 0, 0, 0);
                        item_list_hero.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), HeroFullActivity.class);
                                intent.putExtra(HERO_NAME, hero.split("\\^")[0]);
                                intent.putExtra(HeroFullActivity.HERO_CODE_NAME, hero.split("\\^")[0]);
                                startActivity(intent);
                            }
                        });
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                ll_fragment_guide_worst_versus.addView(item_list_hero);
                                Picasso.get().load("file:///android_asset/heroes/" + hero.split("\\^")[0] + "/full.webp")
                                        .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                        .into(iv_item_image);
                            }
                        });
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initViews() {
        tv_fragment_guide_lane = view.findViewById(R.id.tv_fragment_guide_lane);
        tv_fragment_guide_lane.setCharacterList(TickerUtils.getDefaultListForUSCurrency());
        tv_fragment_guide_time = view.findViewById(R.id.tv_fragment_guide_time);
//        tv_fragment_guide_current_pickrate = view.findViewById(R.id.tv_fragment_guide_current_pickrate);
        ll_fragment_guide_best_versus = view.findViewById(R.id.ll_fragment_guide_best_versus);
        ll_fragment_guide_worst_versus = view.findViewById(R.id.ll_fragment_guide_worst_versus);
        ll_fragment_guide_starting_items = view.findViewById(R.id.ll_fragment_guide_starting_items);
        ll_fragment_guide_further_items = view.findViewById(R.id.ll_fragment_guide_further_items);
        ll_fragment_guide_skill_builds = view.findViewById(R.id.ll_fragment_guide_skill_builds);
        pb_fragment_guide = getActivity().findViewById(R.id.pb_fragment_guide);
    }

}
