package n7.ad2.ui.heroGuide

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import n7.ad2.R
import n7.ad2.databinding.FragmentGuideBinding
import n7.ad2.databinding.ItemListGuideItemBinding
import n7.ad2.databinding.ItemListHeroCompareBinding
import n7.ad2.heroes.db.HeroModel
import n7.ad2.heroes.full.GuideWorker
import n7.ad2.items.full.ItemFullActivity
import n7.ad2.ui.heroPage.HeroPageActivity
import java.util.*

class GuideFragment : Fragment() {
    private var currentPage = 0
    private val hero: HeroModel? = null
    private val hashMapSpells = HashMap<String, String>()
    private var maxItemsInRow = 0
    private var _binding: FragmentGuideBinding? = null
    private var binding = _binding!!
    private var menuSelected: MenuItem? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        currentPage = 0
        inflater.inflate(R.menu.menu_fragment_guide_5_button, menu)
        menuSelected = menu.findItem(R.id.menu_fragment_guide_1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        revertCurrentMenu()
        menuSelected = item
        when (item.itemId) {
            R.id.menu_fragment_guide_1 -> {
                item.setIcon(R.drawable.ic_menu_fragment_guide_1selected)
                currentPage = 0
            }
            R.id.menu_fragment_guide_2 -> {
                item.setIcon(R.drawable.ic_menu_fragment_guide_2selected)
                currentPage = 1
            }
            R.id.menu_fragment_guide_3 -> {
                item.setIcon(R.drawable.ic_menu_fragment_guide_3selected)
                currentPage = 2
            }
            R.id.menu_fragment_guide_4 -> {
                item.setIcon(R.drawable.ic_menu_fragment_guide_4selected)
                currentPage = 3
            }
            R.id.menu_fragment_guide_5 -> {
                item.setIcon(R.drawable.ic_menu_fragment_guide_5selected)
                currentPage = 4
            }
        }
        loadPage()
        return super.onOptionsItemSelected(item)
    }

    private fun revertCurrentMenu() {
        when (currentPage) {
            0 -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_1)
            1 -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_2)
            2 -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_3)
            3 -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_4)
            4 -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_5)
            else -> menuSelected!!.setIcon(R.drawable.ic_menu_fragment_guide_1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        calculateMaxItemInRow()
        setObservers()
    }

    private fun setObservers() {
//        viewModel.jsonArrayHeroAbilities.observe(this, new Observer<JSONArray>() {
//            @Override
//            public void onChanged(@Nullable JSONArray jsonArray) {
//                if (jsonArray != null) {
//                    hashMapSpells.clear();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        try {
//                            String skillName = jsonArray.getJSONObject(i).getString("name").toLowerCase();
//                            hashMapSpells.put(skillName, String.valueOf(i + 1));
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        hashMapSpells.put("shadowraze", "1");
//                        hashMapSpells.put("whirling axes (melee)", "3");
//                        hashMapSpells.put("whirling axes (ranged)", "3");
//                    }
//                }
//            }
//        });
//        viewModel.heroesDao.getHeroByCodeName(viewModel.heroCode).observe(this, new Observer<HeroModel>() {
//            @Override
//            public void onChanged(@Nullable HeroModel heroModel) {
//                if (heroModel != null) {
//                    hero = heroModel;
//                    addHeroes(heroModel.getBestVersus(), getResources().getColor(android.R.color.holo_green_light), binding.llFragmentGuideBest);
//                    addHeroes(hero.getWorstVersus(), getResources().getColor(android.R.color.holo_red_light), binding.llFragmentGuideWorst);
//                    loadPage();
//                }
//            }
//        });
    }

    private fun loadPage() {
        if (hero != null) {
            inflateItems(hero.furtherItems)
            inflateSkillBuild(hero.skillBuilds)
            setLane(hero.lane)
            setDate(hero.time)
        }
    }

    private fun setDate(date: String) {
        val timeArrays = date.split("\\+".toRegex()).toTypedArray()
        binding!!.tvFragmentGuideDate.text = getString(R.string.fragment_guide_date, timeArrays[currentPage])
    }

    private fun setLane(lanes: String) {
        val laneArrays = lanes.split("\\+".toRegex()).toTypedArray()
        binding!!.tvFragmentGuideLane.text = getString(R.string.fragment_guide_lane, laneArrays[currentPage])
    }

    private fun inflateSkillBuild(skillBuilds: String) {
        binding!!.llFragmentGuideSkills.removeAllViews()
        val pages = skillBuilds.split("\\+".toRegex()).toTypedArray()
        val spells = pages[currentPage].split("/".toRegex()).toTypedArray()
        if (spells.size == 1) {
            return
        }
        var countSpell = 1
        for (spell in spells) {
            val itemListGuideItemBinding: ItemListGuideItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_list_guide_item, binding!!.llFragmentGuideSkills, false)
            itemListGuideItemBinding.tvItemListGuideItem.gravity = Gravity.END
            if (countSpell == 17) countSpell = 18
            if (countSpell == 19) countSpell = 20
            if (countSpell == 21) countSpell = 25
            itemListGuideItemBinding.tvItemListGuideItem.text = countSpell.toString()
            itemListGuideItemBinding.tvItemListGuideItem.visibility = View.VISIBLE
            if (spell == "talent") {
                Picasso.get().load(R.drawable.hero_talent)
                    .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder)
                    .into(itemListGuideItemBinding.ivItemListGuideItem)
            } else {
//                Picasso.get().load("file:///android_asset/heroes/" + viewModel.heroCode + "/" + hashMapSpells.get(spell.toLowerCase()) + ".webp")
//                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder)
//                        .into(itemListGuideItemBinding.ivItemListGuideItem);
            }
            countSpell++
            binding!!.llFragmentGuideSkills.addView(itemListGuideItemBinding.root)
        }
    }

    private fun calculateMaxItemInRow() {
        if (activity != null) {
//            val displayMetrics = activity!!.resources.displayMetrics
//            //            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//            val dpWidth = displayMetrics.widthPixels / displayMetrics.density
//            val item_list_hero = layoutInflater.inflate(R.layout.item_list_guide_item, binding!!.llFragmentGuideItems)
//            item_list_hero.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
//            maxItemsInRow = Math.floor(dpWidth / item_list_hero.measuredWidth * displayMetrics.density.toDouble()).toInt()
        }
    }

    private fun inflateItems(items: String) {
        binding!!.llFragmentGuideItems.removeAllViews()
        val pages = items.split("\\+".toRegex()).toTypedArray()
        val itemsName = pages[currentPage].split("/".toRegex()).toTypedArray()
        if (itemsName.size == 1) return
        var itemCount = 0
        var linearLayout = LinearLayout(context)
        for (item in itemsName) {
            val itemListGuideItemBinding: ItemListGuideItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_list_guide_item, binding!!.llFragmentGuideItems, false)
            if (item.contains("^")) {
                itemListGuideItemBinding.tvItemListGuideItem.text = item.split("\\^".toRegex()).toTypedArray()[1].trim { it <= ' ' }
                itemListGuideItemBinding.tvItemListGuideItem.visibility = View.VISIBLE
            }
            if (itemCount % maxItemsInRow == 0) {
                linearLayout = LinearLayout(context)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.gravity = Gravity.CENTER
                binding!!.llFragmentGuideItems.addView(linearLayout)
            }
            linearLayout.addView(itemListGuideItemBinding.root)
            Picasso.get().load("file:///android_asset/items/" + item.split("\\^".toRegex()).toTypedArray()[0] + "/full.webp") //                                    .resize(dpSize(88), dpSize(64))
                .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder)
                .into(itemListGuideItemBinding.ivItemListGuideItem, object : Callback {
                    override fun onSuccess() {
                        itemListGuideItemBinding.root.setOnClickListener {
                            val intent = Intent(context, ItemFullActivity::class.java)
                            intent.putExtra(ItemFullActivity.ITEM_CODE_NAME, item.split("\\^".toRegex()).toTypedArray()[0])
                            intent.putExtra(ItemFullActivity.ITEM_NAME, item.split("\\^".toRegex()).toTypedArray()[0])
                            if (activity != null) {
                                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, itemListGuideItemBinding.ivItemListGuideItem, "iv")
                                startActivity(intent, options.toBundle())
                            } else {
                                startActivity(intent)
                            }
                        }
                    }

                    override fun onError(e: Exception) {

//                            itemListGuideItemBinding.getRoot().setVisibility(View.GONE);
                    }
                })
            itemListGuideItemBinding.ivItemListGuideItem.setOnLongClickListener {
                Snackbar.make(itemListGuideItemBinding.root, item.split("\\^".toRegex()).toTypedArray()[0], Snackbar.LENGTH_SHORT).show()
                true
            }
            itemCount++
        }
    }

    private fun addHeroes(bestHeroes: String, textColor: Int, linearLayout: LinearLayout) {
        linearLayout.removeAllViewsInLayout()
        var counter = 0
        val linearLayoutFirst5item = LinearLayout(context)
        linearLayoutFirst5item.orientation = LinearLayout.HORIZONTAL
        linearLayout.addView(linearLayoutFirst5item)
        val linearLayoutSecond5item = LinearLayout(context)
        linearLayoutSecond5item.orientation = LinearLayout.HORIZONTAL
        linearLayout.addView(linearLayoutSecond5item)
        val heroes = bestHeroes.split("/".toRegex()).toTypedArray()
        if (heroes.size == 1) return
        for (hero in heroes) {
            val heroNameAndWinrate = hero.split("\\^".toRegex()).toTypedArray()
            val itemListHeroBinding: ItemListHeroCompareBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_list_hero_compare, linearLayout, false)
            itemListHeroBinding.hero = HeroModel(heroNameAndWinrate[0], heroNameAndWinrate[1])
            itemListHeroBinding.executePendingBindings()
            itemListHeroBinding.tvItemListHero.setTextColor(textColor)
            itemListHeroBinding.ivItemListHero.setPadding(0, 0, 0, 0)
            itemListHeroBinding.root.setOnClickListener {
                val intent = Intent(context, HeroPageActivity::class.java)
                intent.putExtra(HeroPageActivity.HERO_NAME, "")
                //                    intent.putExtra(HERO_CODE_NAME, heroNameAndWinrate[0]);
                startActivity(intent)
                if (activity != null) {
//                        getActivity().finish();
                }
            }
            if (counter >= 4) {
                linearLayoutSecond5item.addView(itemListHeroBinding.root)
            } else {
                linearLayoutFirst5item.addView(itemListHeroBinding.root)
            }
            counter++
        }
    }

    companion object {
        fun newInstance(): GuideFragment {
            return GuideFragment()
        }
    }
}