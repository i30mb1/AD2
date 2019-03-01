package n7.ad2.heroes.full;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import n7.ad2.R;
import n7.ad2.databinding.FragmentHeroPersonalBinding;
import n7.ad2.databinding.ItemHeroDescriptionBinding;
import n7.ad2.databinding.ItemHeroSpellBinding;
import n7.ad2.databinding.ItemHeroSpellDescriptionBinding;
import n7.ad2.databinding.ItemHeroTalentBinding;
import n7.ad2.databinding.ItemTextForDescriptionBinding;
import n7.ad2.utils.Utils;

public class HeroFragment extends Fragment {

    public static final int FILES_IN_FOLDER_NOT_SPELL = 6;
    private String currentLocale;
    private View lastClickedView;
    private JSONObject jsonObjectHeroFull;
    private JSONArray jsonArrayHeroAbilities;
    private boolean subscription = false;
    private FragmentHeroPersonalBinding binding;
    private HeroFulViewModel viewModel;
    private int colorAccentTheme;

    public HeroFragment() {

    }

    public static HeroFragment newInstance() {
        return new HeroFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hero_personal, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(HeroFulViewModel.class);

        subscription = viewModel.userSubscription();
        currentLocale = getString(R.string.language_resource);

        setHasOptionsMenu(true);

        setHeroImage();
        setObservers();
    }

    private void setObservers() {
        viewModel.jsonObjectHeroFull.observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(@Nullable JSONObject jsonObject) {
                jsonObjectHeroFull = jsonObject;
                binding.ivFragmentHeroPersonal.callOnClick();
            }
        });
        viewModel.jsonArrayHeroAbilities.observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(@Nullable JSONArray jsonArray) {
                if (jsonArray != null) {
                    jsonArrayHeroAbilities = jsonArray;
                    inflateAllSpells();
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
        switch (currentLocale) {
            case "ru":
                currentLocale = "eng";
                break;
            default:
            case "eng":
                currentLocale = "ru";
                break;
        }
        return currentLocale;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_switch:
                viewModel.loadHeroDescription(switchLanguage());
                item.setTitle(currentLocale);
                lastClickedView.callOnClick();
                break;
        }
        return true;
    }

    private void inflateTalent() {
        final ItemHeroSpellBinding talent = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell, binding.llFragmentHeroPersonalSpells, false);

        talent.ivItemHeroSpell.setImageResource(R.drawable.hero_talent);
        talent.cvItemHeroSpell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedView(talent.ivItemHeroSpell);
                inflateTalentView();
            }
        });

        binding.llFragmentHeroPersonalSpells.addView(talent.getRoot());
    }

    private void setClickedView(View v) {
        lastClickedView.setBackgroundColor(0x00000000);
        lastClickedView = v;
        v.setBackgroundColor(getColorAccentTheme());
    }

    @SuppressWarnings("ConstantConditions")
    private void inflateAllSpells() {
        inflateTalent();

        try {
            int itemLength = getActivity().getAssets().list("heroes/" + viewModel.heroCode).length;
            for (int i = 1; i < itemLength - FILES_IN_FOLDER_NOT_SPELL; i++) {
                final ItemHeroSpellBinding spell = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell, binding.llFragmentHeroPersonalSpells, false);
                Picasso.get().load("file:///android_asset/heroes/" + viewModel.heroCode + "/" + i + ".webp")
                        .error(R.drawable.spell_placeholder_error)
                        .placeholder(R.drawable.spell_placeholder)
                        .into(spell.ivItemHeroSpell);

                final int spellNumInJson = i - 1;
                spell.cvItemHeroSpell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClickedView(spell.ivItemHeroSpell);
                        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();
                        loadSpell(spellNumInJson);
                    }
                });
                binding.llFragmentHeroPersonalSpells.addView(spell.getRoot());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void inflateTalentView() {
        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();

        try {
            JSONArray talents = jsonObjectHeroFull.getJSONArray("talents");

            for (int i = 0; i < 4; i++) {
                ItemHeroTalentBinding talent = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_talent, null, false);
                talent.tvItemHeroTalentLeft.setText(talents.get(i).toString().split(":")[0]);
                talent.tvItemHeroTalentRight.setText(talents.get(i).toString().split(":")[1]);
                binding.llFragmentHeroPersonalDescriptions.addView(talent.getRoot());
            }

            if (jsonObjectHeroFull.has("talentsTips")) {
                try {
                    ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
                    description.tvItemHeroDescription.setText(R.string.hero_fragment_tips);

                    for (int i = 0; i < jsonObjectHeroFull.getJSONArray("talentsTips").length(); i++) {
                        ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                        String tips = jsonObjectHeroFull.getJSONArray("talentsTips").get(i).toString();
                        text.tvItemTextForDescriptionText.setText(tips);
                        description.llItemHeroDescription.addView(text.getRoot());
                    }

                    binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadSpell(int spellNum) {
        try {
            JSONObject jsonObjectSpell = (JSONObject) jsonArrayHeroAbilities.get(spellNum);
            inflateSpellDescription(jsonObjectSpell);
            inflateNotes(jsonObjectSpell);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void inflateNotes(JSONObject jsonObjectSpell) {
        if (jsonObjectSpell.has("notes") && subscription) {
            ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
            description.tvItemHeroDescription.setText(R.string.hero_fragment_notes);

            try {
                for (int i = 0; i < jsonObjectSpell.getJSONArray("notes").length(); i++) {
                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                    String notes = jsonObjectSpell.getJSONArray("notes").get(i).toString();
                    text.tvItemTextForDescriptionText.setText(notes);
                    description.llItemHeroDescription.addView(text.getRoot());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
        }
    }

    private void inflateSpellDescription(JSONObject jsonObjectSpell) {
        ItemHeroSpellDescriptionBinding spellDescription = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell_description, binding.llFragmentHeroPersonalDescriptions, false);

        if (jsonObjectSpell.has("name")) {
            try {
                String name = jsonObjectSpell.getString("name");
                spellDescription.tvItemHeroSpellDescriptionName.setText(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("hot_key")) {
            try {
                String hotKey = jsonObjectSpell.getString("hot_key");
                spellDescription.tvItemHeroSpellDescriptionKey.setText(hotKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("legacy_key")) {
            try {
                String legacy_key = jsonObjectSpell.getString("legacy_key");
                spellDescription.tvItemHeroSpellDescriptionOldKey.setText(legacy_key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("effects")) {
            try {
                switch (jsonObjectSpell.getJSONArray("effects").length()) {
                    case 3:
                        spellDescription.tvItemHeroSpellDescriptionType.setVisibility(View.VISIBLE);
                        String effect = jsonObjectSpell.getJSONArray("effects").get(2).toString();
                        spellDescription.tvItemHeroSpellDescriptionType.setText(effect);
                    case 2:
                        spellDescription.tvItemHeroSpellDescriptionArea.setVisibility(View.VISIBLE);
                        effect = jsonObjectSpell.getJSONArray("effects").get(1).toString();
                        spellDescription.tvItemHeroSpellDescriptionArea.setText(effect);
                    case 1:
                        spellDescription.tvItemHeroSpellDescriptionTarget.setVisibility(View.VISIBLE);
                        effect = jsonObjectSpell.getJSONArray("effects").get(0).toString();
                        spellDescription.tvItemHeroSpellDescriptionTarget.setText(effect);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("mana")) {
            try {
                String mana = jsonObjectSpell.getString("mana");
                spellDescription.tvItemHeroSpellDescriptionMp.setText(mana);
                spellDescription.llItemHeroSpellDescriptionMp.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("cooldown")) {
            try {
                String cooldown = jsonObjectSpell.getString("cooldown");
                spellDescription.tvHeroSpellDescriptionCooldown.setText(cooldown);
                spellDescription.llItemHeroSpellDescriptionCooldown.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObjectSpell.has("description")) {
            try {
                for (int i = 0; i < jsonObjectSpell.getJSONArray("description").length(); i++) {
                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                    String description = jsonObjectSpell.getJSONArray("description").get(i).toString();
                    text.tvItemTextForDescriptionText.setText(description);
                    spellDescription.llItemHeroSpellDescription.addView(text.getRoot());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        binding.llFragmentHeroPersonalDescriptions.addView(spellDescription.getRoot());
    }

    private void setHeroImage() {
        lastClickedView = binding.ivFragmentHeroPersonal;
        binding.ivFragmentHeroPersonal.setImageDrawable(Utils.getDrawableFromAssets(getActivity(), String.format("heroes/%s/full.webp", viewModel.heroCode)));
        binding.ivFragmentHeroPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickedView(view);
                loadHeroDescription();
            }
        });
        //start animation
        ((HeroFullActivity) getActivity()).scheduleStartPostponedTransition(binding.ivFragmentHeroPersonal);
    }

    private void loadHeroDescription() {
        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();
        if (jsonObjectHeroFull.has("desc")) {
            try {
                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
                description.tvItemHeroDescription.setText(R.string.hero_fragment_description);

                ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                text.tvItemTextForDescriptionText.setText(jsonObjectHeroFull.getString("desc"));
                text.tvItemTextForDescriptionDash.setVisibility(View.GONE);
                description.llItemHeroDescription.addView(text.getRoot());

                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObjectHeroFull.has("bio")) {
            try {
                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
                description.tvItemHeroDescription.setText(R.string.hero_fragment_bio);

                ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                text.tvItemTextForDescriptionText.setText(jsonObjectHeroFull.getString("bio"));
                text.tvItemTextForDescriptionDash.setVisibility(View.GONE);
                description.llItemHeroDescription.addView(text.getRoot());

                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObjectHeroFull.has("trivia") && subscription) {
            try {
                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
                description.tvItemHeroDescription.setText(R.string.hero_fragment_trivia);

                for (int i = 0; i < jsonObjectHeroFull.getJSONArray("trivia").length(); i++) {
                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                    String trivia = jsonObjectHeroFull.getJSONArray("trivia").get(i).toString();
                    text.tvItemTextForDescriptionText.setText(trivia);
                    description.llItemHeroDescription.addView(text.getRoot());
                }

                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObjectHeroFull.has("tips") && subscription) {
            try {
                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
                description.tvItemHeroDescription.setText(R.string.hero_fragment_tips);

                for (int i = 0; i < jsonObjectHeroFull.getJSONArray("tips").length(); i++) {
                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
                    String tips = jsonObjectHeroFull.getJSONArray("tips").get(i).toString();
                    text.tvItemTextForDescriptionText.setText(tips);
                    description.llItemHeroDescription.addView(text.getRoot());
                }

                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getColorAccentTheme() {
        if (colorAccentTheme == 0 && getContext() != null) {
            TypedValue typedValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
            colorAccentTheme = typedValue.data;
            return typedValue.data;
        } else {
            return colorAccentTheme;
        }
    }

}
