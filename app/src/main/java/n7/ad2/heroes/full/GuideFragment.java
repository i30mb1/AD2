package n7.ad2.heroes.full;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import n7.ad2.R;
import n7.ad2.databinding.FragmentGuideBinding;
import n7.ad2.databinding.ItemListGuideItemBinding;
import n7.ad2.databinding.ItemListHeroBinding;
import n7.ad2.databinding.ItemListHeroCompareBinding;
import n7.ad2.heroes.db.HeroModel;
import n7.ad2.items.full.ItemFullActivity;

import static n7.ad2.heroes.full.HeroFullActivity.HERO_CODE_NAME;
import static n7.ad2.heroes.full.HeroFullActivity.HERO_NAME;
import static n7.ad2.items.full.ItemFullActivity.ITEM_CODE_NAME;
import static n7.ad2.items.full.ItemFullActivity.ITEM_NAME;

public class GuideFragment extends Fragment {

    private int currentPage = 0;
    private HeroModel hero;
    private HashMap<String, String> hashMapSpells = new HashMap<>();
    private int maxItemsInRow = 0;
    private HeroFulViewModel viewModel;
    private FragmentGuideBinding binding;
    private MenuItem menuSelected;

    public GuideFragment() {
        // Required empty public constructor
    }

    public static GuideFragment newInstance() {
        return new GuideFragment();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (viewModel.userSubscription()) {
            inflater.inflate(R.menu.menu_fragment_guide_5_button, menu);
        } else {
            inflater.inflate(R.menu.menu_fragment_guide_2_button, menu);
        }
        menuSelected = menu.findItem(R.id.menu_fragment_guide_1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        revertCurrentMenu();
        menuSelected = item;
        switch (item.getItemId()) {
            case R.id.menu_fragment_guide_1:
                item.setIcon(R.drawable.ic_menu_fragment_guide_1selected);
                currentPage = 0;
                break;
            case R.id.menu_fragment_guide_2:
                item.setIcon(R.drawable.ic_menu_fragment_guide_2selected);
                currentPage = 1;
                break;
            case R.id.menu_fragment_guide_3:
                item.setIcon(R.drawable.ic_menu_fragment_guide_3selected);
                currentPage = 2;
                break;
            case R.id.menu_fragment_guide_4:
                item.setIcon(R.drawable.ic_menu_fragment_guide_4selected);
                currentPage = 3;
                break;
            case R.id.menu_fragment_guide_5:
                item.setIcon(R.drawable.ic_menu_fragment_guide_5selected);
                currentPage = 4;
                break;
        }
        loadPage();
        return super.onOptionsItemSelected(item);
    }

    private void revertCurrentMenu() {
        switch (currentPage) {
            default:
            case 0:
                menuSelected.setIcon(R.drawable.ic_menu_fragment_guide_1);
                break;
            case 1:
                menuSelected.setIcon(R.drawable.ic_menu_fragment_guide_2);
                break;
            case 2:
                menuSelected.setIcon(R.drawable.ic_menu_fragment_guide_3);
                break;
            case 3:
                menuSelected.setIcon(R.drawable.ic_menu_fragment_guide_4);
                break;
            case 4:
                menuSelected.setIcon(R.drawable.ic_menu_fragment_guide_5);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(HeroFulViewModel.class);
        binding.setViewModel(viewModel);

        setHasOptionsMenu(true);

        calculateMaxItemInRow();
        setObservers();
    }

    private void setObservers() {
        viewModel.jsonArrayHeroAbilities.observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(@Nullable JSONArray jsonArray) {
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            String skillName = jsonArray.getJSONObject(i).getString("name").toLowerCase();
                            if (skillName.startsWith("shadowraze")) {
                                hashMapSpells.put("shadowraze", String.valueOf(i + 1).toLowerCase());
                            } else if (skillName.startsWith("whirling axes")) {
                                hashMapSpells.put("whirling axes (melee)", String.valueOf(i + 1).toLowerCase());
                                hashMapSpells.put("whirling axes (ranged)", String.valueOf(i + 1).toLowerCase());
                            } else {
                                hashMapSpells.put(skillName, String.valueOf(i + 1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        viewModel.heroesDao.getHeroByCodeName(viewModel.heroCode).observe(this, new Observer<HeroModel>() {
            @Override
            public void onChanged(@Nullable HeroModel heroModel) {
                if (heroModel != null) {
                    hero = heroModel;
                    addHeroes(heroModel.getBestVersus(), getResources().getColor(android.R.color.holo_green_light), binding.llFragmentGuideBest);
                    addHeroes(hero.getWorstVersus(), getResources().getColor(android.R.color.holo_red_light), binding.llFragmentGuideWorst);
                    loadPage();
                }
            }
        });
    }

    private void loadPage() {
        if (hero != null) {
            inflateItems(hero.getFurtherItems());
            inflateSkillBuild(hero.getSkillBuilds());
            setLane(hero.getLane());
            setDate(hero.getTime());
        }
    }

    private void setDate(String date) {
        String timeArrays[] = date.split("\\+");
        binding.tvFragmentGuideDate.setText(getString(R.string.fragment_guide_date, timeArrays[currentPage]));
    }

    private void setLane(String lanes) {
        String laneArrays[] = lanes.split("\\+");
        binding.tvFragmentGuideLane.setText(getString(R.string.fragment_guide_lane, laneArrays[currentPage]));
    }

    private void inflateSkillBuild(final String skillBuilds) {
        binding.llFragmentGuideSkills.removeAllViews();
        String[] pages = skillBuilds.split("\\+");
        String[] spells = pages[currentPage].split("/");
        if (spells.length == 1) {
            return;
        }
        int countSpell = 1;
        for (final String spell : spells) {
            ItemListGuideItemBinding itemListGuideItemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_list_guide_item, binding.llFragmentGuideSkills, false);
            itemListGuideItemBinding.tvItemListGuideItem.setGravity(Gravity.END);
            if (countSpell == 17) countSpell = 18;
            if (countSpell == 19) countSpell = 20;
            if (countSpell == 21) countSpell = 25;
            itemListGuideItemBinding.tvItemListGuideItem.setText(String.valueOf(countSpell));
            itemListGuideItemBinding.tvItemListGuideItem.setVisibility(View.VISIBLE);
            if (spell.equals("talent")) {
                Picasso.get().load(R.drawable.hero_talent)
                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                        .into(itemListGuideItemBinding.ivItemListGuideItem);
            } else {
                Picasso.get().load("file:///android_asset/heroes/" + viewModel.heroCode + "/" + hashMapSpells.get(spell.toLowerCase()) + ".webp")
                        .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                        .into(itemListGuideItemBinding.ivItemListGuideItem);
            }
            countSpell++;
            binding.llFragmentGuideSkills.addView(itemListGuideItemBinding.getRoot());
        }
    }

    private void calculateMaxItemInRow() {
        if (getActivity() != null) {
            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
//            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            View item_list_hero = getLayoutInflater().inflate(R.layout.item_list_guide_item, binding.llFragmentGuideItems);
            item_list_hero.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);

            maxItemsInRow = (int) Math.floor(dpWidth / item_list_hero.getMeasuredWidth() * displayMetrics.density);
        }
    }

    private void inflateItems(final String items) {
        binding.llFragmentGuideItems.removeAllViews();
        String[] pages = items.split("\\+");
        String[] itemsName = pages[currentPage].split("/");
        if(itemsName.length==1) return;
        int itemCount = 0;
        LinearLayout linearLayout = new LinearLayout(getContext());
        for (final String item : itemsName) {
            final ItemListGuideItemBinding itemListGuideItemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_list_guide_item, binding.llFragmentGuideItems, false);
            if (item.contains("^")) {
                itemListGuideItemBinding.tvItemListGuideItem.setText(item.split("\\^")[1].trim());
                itemListGuideItemBinding.tvItemListGuideItem.setVisibility(View.VISIBLE);
            }
            if (itemCount % maxItemsInRow == 0) {
                linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);
                binding.llFragmentGuideItems.addView(linearLayout);
            }
            linearLayout.addView(itemListGuideItemBinding.getRoot());

            Picasso.get().load("file:///android_asset/items/" + item.split("\\^")[0] + "/full.webp")
//                                    .resize(dpSize(88), dpSize(64))
                    .placeholder(R.drawable.item_placeholder).error(R.drawable.item_placeholder_error)
                    .into(itemListGuideItemBinding.ivItemListGuideItem, new Callback() {
                        @Override
                        public void onSuccess() {
                            itemListGuideItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ItemFullActivity.class);
                                    intent.putExtra(ITEM_CODE_NAME, item.split("\\^")[0]);
                                    intent.putExtra(ITEM_NAME, item.split("\\^")[0]);
                                    if (getActivity() != null) {
                                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), itemListGuideItemBinding.ivItemListGuideItem, "iv");
                                        startActivity(intent, options.toBundle());
                                    } else {
                                        startActivity(intent);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });

            itemCount++;
        }
    }

    private void addHeroes(final String bestHeroes, int textColor, LinearLayout linearLayout) {
        linearLayout.removeAllViewsInLayout();

        int counter = 0;
        LinearLayout linearLayoutFirst5item = new LinearLayout(getContext());
        linearLayoutFirst5item.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(linearLayoutFirst5item);

        LinearLayout linearLayoutSecond5item = new LinearLayout(getContext());
        linearLayoutSecond5item.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(linearLayoutSecond5item);

        String[] heroes = bestHeroes.split("/");
        if (heroes.length == 1) return;
        for (final String hero : heroes) {
            final String[] heroNameAndWinrate = hero.split("\\^");
            ItemListHeroCompareBinding itemListHeroBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_list_hero_compare, linearLayout, false);
            itemListHeroBinding.setHero(new HeroModel(heroNameAndWinrate[0], heroNameAndWinrate[1]));
            itemListHeroBinding.executePendingBindings();
            itemListHeroBinding.tvItemListHero.setTextColor(textColor);
            itemListHeroBinding.ivItemListHero.setPadding(0, 0, 0, 0);
            itemListHeroBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), HeroFullActivity.class);
                    intent.putExtra(HERO_NAME, "");
                    intent.putExtra(HERO_CODE_NAME, heroNameAndWinrate[0]);
                    startActivity(intent);
                    if (getActivity() != null) {
//                        getActivity().finish();
                    }
                }
            });
            if (counter >= 4) {
                linearLayoutSecond5item.addView(itemListHeroBinding.getRoot());
            } else {
                linearLayoutFirst5item.addView(itemListHeroBinding.getRoot());
            }
            counter++;
        }
    }

}
