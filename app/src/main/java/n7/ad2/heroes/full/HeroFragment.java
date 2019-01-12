package n7.ad2.heroes.full;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import n7.ad2.R;
import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.Utils;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;

public class HeroFragment extends Fragment {

    public static final int FILES_IN_FOLDER_NOT_SPELL = 7;
    private String heroFolder;
    private AppExecutors appExecutors;
    private String currentLanguage;
    private View view;
    private LinearLayout ll_fragment_hero_personal_descriptions;
    private ImageView previousView;
    private JSONObject jsonHeroDescription;
    private JSONArray jsonArrayHeroSpells;
    private boolean loadWithError = false;
    private boolean subscription = false;

    public HeroFragment() {

    }

    public static HeroFragment newInstance(String heroFolder, AppExecutors appExecutors) {
        HeroFragment fragment = new HeroFragment();
        fragment.heroFolder = heroFolder;
        fragment.appExecutors = appExecutors;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hero_personal, container, false);
        subscription = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SUBSCRIPTION_PREF, false);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        currentLanguage = getString(R.string.language_resource);
        loadHeroDescriptionFile(currentLanguage);
        setHeroImage();
        setHeroSpells();

        return view;
    }

    private void loadHeroDescriptionFile(final String language) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonHeroDescription = new JSONObject(Utils.readJSONFromAsset(getActivity(), "heroes/" + heroFolder + "/" + language + "_abilities.json"));
                    jsonArrayHeroSpells = (JSONArray) jsonHeroDescription.get("abilities");
                    loadWithError = false;
                    loadHeroDescription();
                } catch (JSONException e) {
                    loadWithError = true;
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    loadWithError = true;
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (subscription) inflater.inflate(R.menu.menu_activity_items_change_language, menu);
    }

    public String switchLanguage() {
        switch (currentLanguage) {
            case "ru":
                currentLanguage = "eng";
                break;
            default:
            case "eng":
                currentLanguage = "ru";
                break;
        }
        return currentLanguage;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch:
                loadHeroDescriptionFile(switchLanguage());
                item.setTitle(currentLanguage);
                previousView.callOnClick();
                break;
        }
        return true;
    }

    private void setHeroSpells() {
        if (loadWithError) return;
        final LinearLayout ll_fragment_hero_personal_spells = view.findViewById(R.id.ll_fragment_hero_personal_spells);

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
//                AsyncLayoutInflater inflater = new AsyncLayoutInflater(getActivity());
//                inflater.inflate(R.layout.item_hero_personal_spell, ll_fragment_hero_personal_spells, new AsyncLayoutInflater.OnInflateFinishedListener() {
//                    @Override
//                    public void onInflateFinished(@NonNull View view, int i, @Nullable ViewGroup viewGroup) {
//
//                    }
//                });
                final View item_hero_personal_talent = getLayoutInflater().inflate(R.layout.item_hero_personal_spell, null);
                final ImageView iv_item_talent = item_hero_personal_talent.findViewById(R.id.iv_item);
                iv_item_talent.setImageResource(R.drawable.hero_talent);
                item_hero_personal_talent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previousView.setBackgroundColor(0x00000000);
                        previousView = iv_item_talent;
                        iv_item_talent.setBackgroundColor(getColorAccentTheme());
                        ll_fragment_hero_personal_descriptions.removeAllViewsInLayout();
                        final View item_hero_personal_talent = getLayoutInflater().inflate(R.layout.item_hero_personal_description_talent, null);
                        try {
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv)).setText(getString(R.string.item_hero_personal_description_talents));
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_25_left)).setText(jsonHeroDescription.getJSONArray("talents").get(0).toString().split(":")[0]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_25_right)).setText(jsonHeroDescription.getJSONArray("talents").get(0).toString().split(":")[1]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_20_left)).setText(jsonHeroDescription.getJSONArray("talents").get(1).toString().split(":")[0]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_20_right)).setText(jsonHeroDescription.getJSONArray("talents").get(1).toString().split(":")[1]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_15_left)).setText(jsonHeroDescription.getJSONArray("talents").get(2).toString().split(":")[0]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_15_right)).setText(jsonHeroDescription.getJSONArray("talents").get(2).toString().split(":")[1]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_10_left)).setText(jsonHeroDescription.getJSONArray("talents").get(3).toString().split(":")[0]);
                            ((TextView) item_hero_personal_talent.findViewById(R.id.tv_10_right)).setText(jsonHeroDescription.getJSONArray("talents").get(3).toString().split(":")[1]);

                            LinearLayout ll_items_descriptionFirst = item_hero_personal_talent.findViewById(R.id.ll_item_description);
                            if (jsonHeroDescription.has("talentsTips"))
                                for (int i = 0; i < jsonHeroDescription.getJSONArray("talentsTips").length(); i++) {
                                    View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
                                    ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonHeroDescription.getJSONArray("talentsTips").get(i).toString());
                                    ll_items_descriptionFirst.addView(item_description);
                                }
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_talent);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        ll_fragment_hero_personal_spells.addView(item_hero_personal_talent);
                    }
                });

                if (getActivity() != null)
                    try {
                        int itemLengths = getActivity().getAssets().list("heroes/" + heroFolder).length;
                        for (int i = 1; i <= itemLengths - FILES_IN_FOLDER_NOT_SPELL; i++) {
                            final View item_hero_personal_spell = getLayoutInflater().inflate(R.layout.item_hero_personal_spell, null);
                            final ImageView iv_item = item_hero_personal_spell.findViewById(R.id.iv_item);
                            final int finalI = i;
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.get().load("file:///android_asset/heroes/" + heroFolder + "/" + finalI + ".webp")
                                            .error(R.drawable.spell_placeholder_error)
                                            .placeholder(R.drawable.spell_placeholder)
                                            .into(iv_item);
                                }
                            });

                            final int spellNum = i - 1;
                            item_hero_personal_spell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    previousView.setBackgroundColor(0x00000000);
                                    previousView = iv_item;
                                    iv_item.setBackgroundColor(getColorAccentTheme());

                                    ll_fragment_hero_personal_descriptions.removeAllViewsInLayout();
                                    try {
                                        loadSpell(spellNum);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    ll_fragment_hero_personal_spells.addView(item_hero_personal_spell);
                                }
                            });

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    private void loadSpell(int spellNum) throws JSONException {
        if (loadWithError) return;
        JSONObject jsonObjectSpell = (JSONObject) jsonArrayHeroSpells.get(spellNum);
        final View item_hero_personal_spell = getLayoutInflater().inflate(R.layout.item_hero_personal_description, null);
        ((TextView) item_hero_personal_spell.findViewById(R.id.tv)).setText(jsonObjectSpell.getString("name"));
        if (jsonObjectSpell.has("hot_key"))
            ((TextView) item_hero_personal_spell.findViewById(R.id.tv_hotkey)).setText(jsonObjectSpell.getString("hot_key"));
        if (jsonObjectSpell.has("legacy_key"))
            ((TextView) item_hero_personal_spell.findViewById(R.id.tv_oldhotkey)).setText(jsonObjectSpell.getString("legacy_key"));
        ((TextView) item_hero_personal_spell.findViewById(R.id.ll_item_description)).setText(jsonObjectSpell.getString("spell"));

        item_hero_personal_spell.findViewById(R.id.tv_item_type).setVisibility(View.GONE);
        item_hero_personal_spell.findViewById(R.id.tv_item_uses).setVisibility(View.GONE);
        switch (jsonObjectSpell.getJSONArray("effects").length()) {
            case 3:
                item_hero_personal_spell.findViewById(R.id.tv_item_type).setVisibility(View.VISIBLE);
                ((TextView) item_hero_personal_spell.findViewById(R.id.tv_item_type)).setText(jsonObjectSpell.getJSONArray("effects").get(2).toString());
            case 2:
                item_hero_personal_spell.findViewById(R.id.tv_item_uses).setVisibility(View.VISIBLE);
                ((TextView) item_hero_personal_spell.findViewById(R.id.tv_item_uses)).setText(jsonObjectSpell.getJSONArray("effects").get(1).toString());
            case 1:
                ((TextView) item_hero_personal_spell.findViewById(R.id.tv_item_area)).setText(jsonObjectSpell.getJSONArray("effects").get(0).toString());
        }

        if (jsonObjectSpell.has("mana")) {
            ((TextView) item_hero_personal_spell.findViewById(R.id.tv_item_mp)).setText(jsonObjectSpell.getString("mana"));
        } else {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    item_hero_personal_spell.findViewById(R.id.tv_item_mp).setVisibility(View.GONE);
                    item_hero_personal_spell.findViewById(R.id.iv_mp).setVisibility(View.GONE);
                }
            });
        }
        if (jsonObjectSpell.has("cooldown")) {
            ((TextView) item_hero_personal_spell.findViewById(R.id.tv_item_cooldown)).setText(jsonObjectSpell.getString("cooldown"));
        } else {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    item_hero_personal_spell.findViewById(R.id.tv_item_cooldown).setVisibility(View.GONE);
                    item_hero_personal_spell.findViewById(R.id.iv_cooldown).setVisibility(View.GONE);
                }
            });
        }

        if (jsonObjectSpell.has("description")) {
            LinearLayout ll_items_descriptionFirst = item_hero_personal_spell.findViewById(R.id.ll_items_description);
            for (int i = 0; i < jsonObjectSpell.getJSONArray("description").length(); i++) {
                View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
                ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonObjectSpell.getJSONArray("description").get(i).toString());
                ll_items_descriptionFirst.addView(item_description);
            }

            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_spell);
                }
            });
        }

        if (jsonObjectSpell.has("notes") && subscription) {
            final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_personal_description_simple, null);
            ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv)).setText(R.string.item_hero_personal_description_notes);
            LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_description);
            for (int i = 0; i < jsonObjectSpell.getJSONArray("notes").length(); i++) {
                View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
                ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonObjectSpell.getJSONArray("notes").get(i).toString());
                ll_items_description.addView(item_description);
            }

            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_description_simple);
//                                            sv_fragment_hero.smoothScrollTo(0,0);
                }
            });
        }
    }

    private void setHeroImage() {
        ll_fragment_hero_personal_descriptions = view.findViewById(R.id.ll_fragment_hero_personal_descriptions);
        final ImageView iv_fragment_hero_personal = view.findViewById(R.id.iv_fragment_hero_personal);
        previousView = iv_fragment_hero_personal;

        iv_fragment_hero_personal.setImageDrawable(Utils.getDrawableFromAssets(getActivity(), String.format("heroes/%s/full.webp", heroFolder)));
        //start animation
        ((HeroFullActivity) getActivity()).scheduleStartPostponedTransition(iv_fragment_hero_personal);

        iv_fragment_hero_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousView.setBackgroundColor(0x00000000);
                previousView = (ImageView) view;
                view.setBackgroundColor(getColorAccentTheme());
                ll_fragment_hero_personal_descriptions.removeAllViewsInLayout();

                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadHeroDescription();
                    }
                });
            }
        });
//        iv_fragment_hero_personal.callOnClick();
    }

    private void loadHeroDescription() {
        if (loadWithError) return;
        if (jsonHeroDescription.has("desc")) {
            final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_personal_description_simple, null);
            ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv)).setText(R.string.item_hero_personal_description_desc);

            LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_description);
            View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
            try {
                ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonHeroDescription.getString("desc"));
                item_description.findViewById(R.id.textView2).setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ll_items_description.addView(item_description);
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_description_simple);
                }
            });
        }
        if (jsonHeroDescription.has("bio")) {
            final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_personal_description_simple, null);
            ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv)).setText(R.string.item_hero_personal_description_bio);

            LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_description);
            View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
            try {
                ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonHeroDescription.getString("bio"));
                item_description.findViewById(R.id.textView2).setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ll_items_description.addView(item_description);
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_description_simple);
                }
            });
        }
        if (jsonHeroDescription.has("trivia") && subscription) {
            final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_personal_description_simple, null);
            ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv)).setText(R.string.item_hero_personal_description_facts);

            try {
                LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_description);
                for (int i = 0; i < jsonHeroDescription.getJSONArray("trivia").length(); i++) {
                    View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
                    ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonHeroDescription.getJSONArray("trivia").get(i).toString());
                    ll_items_description.addView(item_description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_description_simple);
                }
            });
        }
        if (jsonHeroDescription.has("tips") && subscription) {
            final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_personal_description_simple, null);
            ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv)).setText(R.string.item_hero_personal_description_tips);

            try {
                LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_description);
                for (int i = 0; i < jsonHeroDescription.getJSONArray("tips").length(); i++) {
                    View item_description = getLayoutInflater().inflate(R.layout.item_description, null);
                    ((TextView) item_description.findViewById(R.id.tv_description)).setText(jsonHeroDescription.getJSONArray("tips").get(i).toString());
                    ll_items_description.addView(item_description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    ll_fragment_hero_personal_descriptions.addView(item_hero_personal_description_simple);
                }
            });
        }
    }

    private int getColorAccentTheme() {
        if (getContext() != null) {
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
            return typedValue.data;
        }
        return 0;
    }

}
