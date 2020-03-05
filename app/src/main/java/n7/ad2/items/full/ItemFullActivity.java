package n7.ad2.items.full;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;

public class ItemFullActivity extends BaseActivity {

    public final static String ITEM_NAME = "ITEM_NAME";
    public final static String ITEM_CODE_NAME = "ITEM_CODE_NAME";
    private JSONObject jsonItemDescription;
    private String itemFolder;
    private String itemName;
    private String currentLanguage;
    private boolean subscription = true;
    private boolean loadWithError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_full);
//        subscription = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SUBSCRIPTION_PREF, false);

        this.itemFolder = getIntent().getStringExtra(ITEM_CODE_NAME);
        this.itemName = getIntent().getStringExtra(ITEM_NAME);

        setToolbar();
        currentLanguage = getString(R.string.language_resource);
        loadHeroDescriptionFile(currentLanguage);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_switch:
                loadHeroDescriptionFile(switchLanguage());
                item.setTitle(currentLanguage);
                setItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (subscription) {
            getMenuInflater().inflate(R.menu.menu_activity_items_change_language, menu);
        }
        return true;
    }

    public String switchLanguage() {
        switch (currentLanguage) {
            default:
            case "ru":
                currentLanguage = "eng";
                break;
            case "eng":
                currentLanguage = "ru";
                break;
        }
        return currentLanguage;
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(itemName);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);//включаем кнопку домой
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//ставим значок стрелочки на кнопку
        }
    }

    private void setItems() {
        if (loadWithError) return;
        final ImageView iv_activity_item_full_main_image = findViewById(R.id.iv_activity_item_full_main_image);
        iv_activity_item_full_main_image.setImageDrawable(Utils.getDrawableFromAssets(this, String.format("items/%s/full.webp", itemFolder)));
        final TextView tv_activity_item_full_name = findViewById(R.id.tv_activity_item_full_name);
        tv_activity_item_full_name.setText(itemName);
        final TextView tv_activity_item_full_cost = findViewById(R.id.tv_activity_item_full_cost);
        final LinearLayout ll_activity_item_full_descriptions = findViewById(R.id.ll_activity_item_full_descriptions);

        ll_activity_item_full_descriptions.removeAllViewsInLayout();
        try {
            tv_activity_item_full_cost.setText(jsonItemDescription.getString("cost"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //loading Description
        final View item_hero_personal_description_simple = getLayoutInflater().inflate(R.layout.item_hero_description, ll_activity_item_full_descriptions,false);
        ((TextView) item_hero_personal_description_simple.findViewById(R.id.tv_item_hero_description)).setText(R.string.hero_fragment_description);

        LinearLayout ll_items_description = item_hero_personal_description_simple.findViewById(R.id.ll_item_hero_description);
        View item_description = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
        try {
            ((TextView) item_description.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonItemDescription.getString("description"));
            item_description.findViewById(R.id.tv_item_text_for_description_dash).setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ll_items_description.addView(item_description);
        //loading Attrs
        if (jsonItemDescription.has("attrs")) {
            try {
                for (int i = 0; i < jsonItemDescription.getJSONArray("attrs").length(); i++) {
                    View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                    ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonItemDescription.getJSONArray("attrs").get(i).toString());
                    ll_items_description.addView(item_descriptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ll_activity_item_full_descriptions.addView(item_hero_personal_description_simple);

        //load Info
        if (jsonItemDescription.has("info") && subscription) {
            final View item_hero_personal_info_simple = getLayoutInflater().inflate(R.layout.item_hero_description, ll_activity_item_full_descriptions,false);
            ((TextView) item_hero_personal_info_simple.findViewById(R.id.tv_item_hero_description)).setText(R.string.item_hero_personal_info_desc);
            LinearLayout ll_items_info = item_hero_personal_info_simple.findViewById(R.id.ll_item_hero_description);
            try {
                for (int i = 0; i < jsonItemDescription.getJSONArray("info").length(); i++) {
                    View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                    ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonItemDescription.getJSONArray("info").get(i).toString());
                    ll_items_info.addView(item_descriptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ll_activity_item_full_descriptions.addView(item_hero_personal_info_simple);

        }

        //load tips
        if (jsonItemDescription.has("tips") && subscription) {
            final View item_hero_personal_tips_simple = getLayoutInflater().inflate(R.layout.item_hero_description, ll_activity_item_full_descriptions,false);
            ((TextView) item_hero_personal_tips_simple.findViewById(R.id.tv_item_hero_description)).setText(R.string.hero_fragment_tips);
            LinearLayout ll_items_tips = item_hero_personal_tips_simple.findViewById(R.id.ll_item_hero_description);
            try {
                for (int i = 0; i < jsonItemDescription.getJSONArray("tips").length(); i++) {
                    View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                    ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonItemDescription.getJSONArray("tips").get(i).toString());
                    ll_items_tips.addView(item_descriptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ll_activity_item_full_descriptions.addView(item_hero_personal_tips_simple);

        }
        //loading Trivia
        if (jsonItemDescription.has("trivia")) {
            final View item_hero_personal_info_simple = getLayoutInflater().inflate(R.layout.item_hero_description, ll_activity_item_full_descriptions,false);
            ((TextView) item_hero_personal_info_simple.findViewById(R.id.tv_item_hero_description)).setText(R.string.item_hero_personal_trivia_desc);
            LinearLayout ll_items_info = item_hero_personal_info_simple.findViewById(R.id.ll_item_hero_description);
            try {
                for (int i = 0; i < jsonItemDescription.getJSONArray("trivia").length(); i++) {
                    View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                    ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonItemDescription.getJSONArray("trivia").get(i).toString());
                    ll_items_info.addView(item_descriptions);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ll_activity_item_full_descriptions.addView(item_hero_personal_info_simple);
        }

        if (jsonItemDescription.has("abilities")) {
            try {
                for (int i = 0; i < jsonItemDescription.getJSONArray("abilities").length(); i++) {
                    final View item_item_personal_spell = getLayoutInflater().inflate(R.layout.item_item_personal_description, ll_activity_item_full_descriptions, false);
                    JSONObject jsonObject = jsonItemDescription.getJSONArray("abilities").getJSONObject(i);
                    if (jsonObject.has("name")) ((TextView) item_item_personal_spell.findViewById(R.id.tv)).setText(jsonObject.getString("name"));
                    if (jsonObject.has("description"))  ((TextView) item_item_personal_spell.findViewById(R.id.ll_item_description)).setText(jsonObject.getString("description"));

                    LinearLayout linearLayout = item_item_personal_spell.findViewById(R.id.ll_items_description);
                    if (jsonObject.has("mp")) {

                    } else {
                        item_item_personal_spell.findViewById(R.id.iv_mp).setVisibility(View.GONE);
                        item_item_personal_spell.findViewById(R.id.tv_item_mp).setVisibility(View.GONE);
                    }
                    if (jsonObject.has("cooldown")) {

                    } else {
                        item_item_personal_spell.findViewById(R.id.tv_item_cooldown).setVisibility(View.GONE);
                        item_item_personal_spell.findViewById(R.id.iv_cooldown).setVisibility(View.GONE);
                    }

                    if (jsonObject.has("effects")) {
                        for (int i1 = 0; i1 < jsonObject.getJSONArray("effects").length(); i1++) {
                            View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                            ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonObject.getJSONArray("effects").get(i1).toString());
                            linearLayout.addView(item_descriptions);
                        }
                    }
                    if (jsonObject.has("notes") && subscription) {
                        for (int i1 = 0; i1 < jsonObject.getJSONArray("notes").length(); i1++) {
                            View item_descriptions = getLayoutInflater().inflate(R.layout.item_text_for_description, null);
                            ((TextView) item_descriptions.findViewById(R.id.tv_item_text_for_description_text)).setText(jsonObject.getJSONArray("notes").get(i1).toString());
                            linearLayout.addView(item_descriptions);
                        }
                    }
                    if (jsonObject.has("elements")) {
                        item_item_personal_spell.findViewById(R.id.tv_item_type).setVisibility(View.GONE);
                        item_item_personal_spell.findViewById(R.id.tv_item_uses).setVisibility(View.GONE);
                        item_item_personal_spell.findViewById(R.id.tv_item_area).setVisibility(View.GONE);
                        switch (jsonObject.getJSONArray("elements").length()) {
                            case 3:
                                item_item_personal_spell.findViewById(R.id.tv_item_type).setVisibility(View.VISIBLE);
                                ((TextView) item_item_personal_spell.findViewById(R.id.tv_item_type)).setText(jsonObject.getJSONArray("elements").get(2).toString());
                            case 2:
                                item_item_personal_spell.findViewById(R.id.tv_item_uses).setVisibility(View.VISIBLE);
                                ((TextView) item_item_personal_spell.findViewById(R.id.tv_item_uses)).setText(jsonObject.getJSONArray("elements").get(1).toString());
                            case 1:
                                item_item_personal_spell.findViewById(R.id.tv_item_area).setVisibility(View.VISIBLE);
                                ((TextView) item_item_personal_spell.findViewById(R.id.tv_item_area)).setText(jsonObject.getJSONArray("elements").get(0).toString());
                        }
                    }
                    ll_activity_item_full_descriptions.addView(item_item_personal_spell);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        iv_activity_item_full_main_image.callOnClick();
    }

    private void loadHeroDescriptionFile(final String language) {
        try {
            String file = Utils.readJSONFromAsset(ItemFullActivity.this, "items/" + itemFolder + "/" + language + "_description.json");
            if (file == null) throw new JSONException("");
            jsonItemDescription = new JSONObject(file);
            setItems();
            loadWithError = false;
        } catch (JSONException e) {
            e.printStackTrace();
            loadWithError = true;
        }
    }
}
