package n7.ad2.ui.heroInfo

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroPersonalBinding
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HeroInfoFragment : Fragment(R.layout.fragment_hero_personal) {

    private var infoPopupWindow: InfoPopupWindow? = null
    private var currentLocale: String? = null
    private var lastClickedView: View? = null
    private val jsonObjectHeroFull: JSONObject? = null
    private val jsonArrayHeroAbilities: JSONArray? = null
    private lateinit var binding: FragmentHeroPersonalBinding
    private val viewModel: HeroInfoViewModel by activityViewModels()
    private lateinit var spellsInfoListAdapter: SpellsInfoListAdapter
    private var colorAccentTheme = 0
        private get() = if (field == 0 && context != null) {
            val typedValue = TypedValue()
            context!!.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            field = typedValue.data
            typedValue.data
        } else {
            field
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroPersonalBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currentLocale = getString(R.string.language_resource)
        setHasOptionsMenu(true)
        setHeroImage()
        setObservers()

        setupSpellRecyclerView()
        setupSpellInfoRecyclerView()
    }

   fun showPopup(view: View) {
        infoPopupWindow = InfoPopupWindow(view)
    }

    override fun onPause() {
        super.onPause()
        infoPopupWindow?.dismiss()
    }

    fun setDescription(voDescription: List<VODescription>) {
        spellsInfoListAdapter.submitList(voDescription)
    }

    private fun setupSpellInfoRecyclerView() {
        spellsInfoListAdapter = SpellsInfoListAdapter(this)

        binding.rvSpellsInfo.apply {
            adapter = spellsInfoListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        spellsInfoListAdapter.submitList(listOf(VOSpell(), VOSpell()))
    }

    private fun setupSpellRecyclerView() {
        val spellsAdapter = SpellsListAdapter()

        binding.rvSpells.apply {
            LinearSnapHelper().attachToRecyclerView(this)
            adapter = spellsAdapter
        }
    }

    private fun setObservers() {
//        viewModel.jsonObjectHeroFull.observe(getViewLifecycleOwner(), new Observer<JSONObject>() {
//            @Override
//            public void onChanged(@Nullable JSONObject jsonObject) {
//                jsonObjectHeroFull = jsonObject;
//                binding.ivFragmentHeroPersonal.callOnClick();
//            }
//        });
//        viewModel.jsonArrayHeroAbilities.observe(getViewLifecycleOwner(), new Observer<JSONArray>() {
//            @Override
//            public void onChanged(@Nullable JSONArray jsonArray) {
//                if (jsonArray != null) {
//                    jsonArrayHeroAbilities = jsonArray;
//                    inflateAllSpells();
//                }
//            }
//        });
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_activity_items_change_language, menu)
    }

    fun switchLanguage(): String {
        currentLocale = when (currentLocale) {
            "ru" -> "eng"
            "eng" -> "ru"
            else -> "ru"
        }
        return currentLocale!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_switch -> {
                viewModel!!.loadHeroDescription(switchLanguage())
                item.title = currentLocale
                lastClickedView!!.callOnClick()
            }
        }
        return true
    }

    private fun inflateTalent() {
//        final ItemHeroSpellBinding talent = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell, binding.llFragmentHeroPersonalSpells, false);
//
//        talent.ivItemHeroSpell.setImageResource(R.drawable.hero_talent);
//        talent.cvItemHeroSpell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setClickedView(talent.ivItemHeroSpell);
//                inflateTalentView();
//            }
//        });
//
//        binding.llFragmentHeroPersonalSpells.addView(talent.getRoot());
    }

    private fun setClickedView(v: View) {
        lastClickedView!!.setBackgroundColor(0x00000000)
        lastClickedView = v
        v.setBackgroundColor(colorAccentTheme)
    }

    private fun inflateAllSpells() {
//        binding.llFragmentHeroPersonalSpells.removeAllViewsInLayout();
//        inflateTalent();
//
//        try {
//            int itemLength = getActivity().getAssets().list("heroes/" + viewModel.heroCode).length;
//            for (int i = 1; i < itemLength - FILES_IN_FOLDER_NOT_SPELL; i++) {
//                final ItemHeroSpellBinding spell = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell, binding.llFragmentHeroPersonalSpells, false);
//                Picasso.get().load("file:///android_asset/heroes/" + viewModel.heroCode + "/" + i + ".webp")
//                        .error(R.drawable.spell_placeholder)
//                        .placeholder(R.drawable.spell_placeholder)
//                        .into(spell.ivItemHeroSpell);
//
//                final int spellNumInJson = i - 1;
//                spell.cvItemHeroSpell.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        setClickedView(spell.ivItemHeroSpell);
//                        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();
//                        loadSpell(spellNumInJson);
//                    }
//                });
//                binding.llFragmentHeroPersonalSpells.addView(spell.getRoot());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
    }

    private fun inflateTalentView() {
//        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();
//
//        try {
//            JSONArray talents = jsonObjectHeroFull.getJSONArray("talents");
//
//            for (int i = 0; i < 4; i++) {
//                ItemHeroTalentBinding talent = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_talent, null, false);
//                talent.tvItemHeroTalentLeft.setText(talents.get(i).toString().split(":")[0]);
//                talent.tvItemHeroTalentRight.setText(talents.get(i).toString().split(":")[1]);
//                binding.llFragmentHeroPersonalDescriptions.addView(talent.getRoot());
//            }
//
//            if (jsonObjectHeroFull.has("talentsTips")) {
//                try {
//                    ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//                    description.tvItemHeroDescription.setText(R.string.hero_fragment_tips);
//
//                    for (int i = 0; i < jsonObjectHeroFull.getJSONArray("talentsTips").length(); i++) {
//                        ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                        String tips = jsonObjectHeroFull.getJSONArray("talentsTips").get(i).toString();
//                        text.tvItemTextForDescriptionText.setText(tips);
//                        description.llItemHeroDescription.addView(text.getRoot());
//                    }
//
//                    binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private fun loadSpell(spellNum: Int) {
        try {
            val jsonObjectSpell = jsonArrayHeroAbilities!![spellNum] as JSONObject
            inflateSpellDescription(jsonObjectSpell)
            inflateNotes(jsonObjectSpell)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun inflateNotes(jsonObjectSpell: JSONObject) {
//        if (jsonObjectSpell.has("notes")) {
//            ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//            description.tvItemHeroDescription.setText(R.string.hero_fragment_notes);
//
//            try {
//                for (int i = 0; i < jsonObjectSpell.getJSONArray("notes").length(); i++) {
//                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                    String notes = jsonObjectSpell.getJSONArray("notes").get(i).toString();
//                    text.tvItemTextForDescriptionText.setText(notes);
//                    description.llItemHeroDescription.addView(text.getRoot());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//        }
    }

    private fun inflateSpellDescription(jsonObjectSpell: JSONObject) {
//        ItemHeroSpellDescriptionBinding spellDescription = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_spell_description, binding.llFragmentHeroPersonalDescriptions, false);
//
//        if (jsonObjectSpell.has("name")) {
//            try {
//                String name = jsonObjectSpell.getString("name");
//                spellDescription.tvItemHeroSpellDescriptionName.setText(name);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (jsonObjectSpell.has("spell")) {
//            try {
//                String name = jsonObjectSpell.getString("spell");
//                spellDescription.tvItemHeroSpellDescriptionText.setText(name);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("hot_key")) {
//            try {
//                String hotKey = jsonObjectSpell.getString("hot_key");
//                spellDescription.tvItemHeroSpellDescriptionKey.setText(hotKey);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("legacy_key")) {
//            try {
//                String legacy_key = jsonObjectSpell.getString("legacy_key");
//                spellDescription.tvItemHeroSpellDescriptionOldKey.setText(legacy_key);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("effects")) {
//            try {
//                switch (jsonObjectSpell.getJSONArray("effects").length()) {
//                    case 3:
//                        spellDescription.tvItemHeroSpellDescriptionType.setVisibility(View.VISIBLE);
//                        String effect = jsonObjectSpell.getJSONArray("effects").get(2).toString();
//                        spellDescription.tvItemHeroSpellDescriptionType.setText(effect);
//                    case 2:
//                        spellDescription.tvItemHeroSpellDescriptionArea.setVisibility(View.VISIBLE);
//                        effect = jsonObjectSpell.getJSONArray("effects").get(1).toString();
//                        spellDescription.tvItemHeroSpellDescriptionArea.setText(effect);
//                    case 1:
//                        spellDescription.tvItemHeroSpellDescriptionTarget.setVisibility(View.VISIBLE);
//                        effect = jsonObjectSpell.getJSONArray("effects").get(0).toString();
//                        spellDescription.tvItemHeroSpellDescriptionTarget.setText(effect);
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("mana")) {
//            try {
//                String mana = jsonObjectSpell.getString("mana");
//                spellDescription.tvItemHeroSpellDescriptionMp.setText(mana);
//                spellDescription.llItemHeroSpellDescriptionMp.setVisibility(View.VISIBLE);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("cooldown")) {
//            try {
//                String cooldown = jsonObjectSpell.getString("cooldown");
//                spellDescription.tvHeroSpellDescriptionCooldown.setText(cooldown);
//                spellDescription.llItemHeroSpellDescriptionCooldown.setVisibility(View.VISIBLE);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (jsonObjectSpell.has("description")) {
//            try {
//                for (int i = 0; i < jsonObjectSpell.getJSONArray("description").length(); i++) {
//                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                    String description = jsonObjectSpell.getJSONArray("description").get(i).toString();
//                    text.tvItemTextForDescriptionText.setText(description);
//                    spellDescription.llItemHeroSpellDescription.addView(text.getRoot());
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        binding.llFragmentHeroPersonalDescriptions.addView(spellDescription.getRoot());
    }

    private fun setHeroImage() {
//        lastClickedView = binding.ivFragmentHeroPersonal;
//        binding.ivFragmentHeroPersonal.setImageDrawable(Utils.getDrawableFromAssets(getActivity(), String.format("heroes/%s/full.webp", viewModel.heroCode)));
//        binding.ivFragmentHeroPersonal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setClickedView(view);
//                loadHeroDescription();
//            }
//        });
//        //start animation
//        ((HeroFullActivity) getActivity()).scheduleStartPostponedTransition(binding.ivFragmentHeroPersonal);
    }

    private fun loadHeroDescription() {
//        binding.llFragmentHeroPersonalDescriptions.removeAllViewsInLayout();
//        if (jsonObjectHeroFull.has("desc")) {
//            try {
//                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//                description.tvItemHeroDescription.setText(R.string.hero_fragment_description);
//
//                ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                text.tvItemTextForDescriptionText.setText(jsonObjectHeroFull.getString("desc"));
//                text.tvItemTextForDescriptionDash.setVisibility(View.GONE);
//                description.llItemHeroDescription.addView(text.getRoot());
//
//                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (jsonObjectHeroFull.has("bio")) {
//            try {
//                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//                description.tvItemHeroDescription.setText(R.string.hero_fragment_bio);
//
//                ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                text.tvItemTextForDescriptionText.setText(jsonObjectHeroFull.getString("bio"));
//                text.tvItemTextForDescriptionDash.setVisibility(View.GONE);
//                description.llItemHeroDescription.addView(text.getRoot());
//
//                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (jsonObjectHeroFull.has("trivia")) {
//            try {
//                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//                description.tvItemHeroDescription.setText(R.string.hero_fragment_trivia);
//
//                for (int i = 0; i < jsonObjectHeroFull.getJSONArray("trivia").length(); i++) {
//                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                    String trivia = jsonObjectHeroFull.getJSONArray("trivia").get(i).toString();
//                    text.tvItemTextForDescriptionText.setText(trivia);
//                    description.llItemHeroDescription.addView(text.getRoot());
//                }
//
//                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        if (jsonObjectHeroFull.has("tips")) {
//            try {
//                ItemHeroDescriptionBinding description = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_hero_description, binding.llFragmentHeroPersonalDescriptions, false);
//                description.tvItemHeroDescription.setText(R.string.hero_fragment_tips);
//
//                for (int i = 0; i < jsonObjectHeroFull.getJSONArray("tips").length(); i++) {
//                    ItemTextForDescriptionBinding text = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_text_for_description, null, false);
//                    String tips = jsonObjectHeroFull.getJSONArray("tips").get(i).toString();
//                    text.tvItemTextForDescriptionText.setText(tips);
//                    description.llItemHeroDescription.addView(text.getRoot());
//                }
//
//                binding.llFragmentHeroPersonalDescriptions.addView(description.getRoot());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }



    companion object {
        const val FILES_IN_FOLDER_NOT_SPELL = 6
        fun newInstance(): HeroInfoFragment {
            return HeroInfoFragment()
        }
    }
}