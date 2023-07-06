package n7.ad2.hero.page.internal.info.domain.adapter

//@ExperimentalStdlibApi
//fun Ability.toVOSpell(application: Application) = VOSpell(
//    this.spellName,
//    HeroRepository.getFullUrlHeroSpell(this.spellName),
//    buildList {
//        add(VOHeroInfo.HeaderSound(spellName, hotKey!!, legacyKey!!, audioUrl!!))
//        effects.forEach { title -> add(VOBodyLine(SpannableString(title.spanWithDotaImages(application, true)))) }
//        talents?.forEach { talent -> add(VOBodyTalent(talent.talentLeft, talent.talentLvl, talent.talentRight)) }
//        description?.let { description -> add(VOBodySimple(description)) }
//        cooldown?.let { cooldown -> add(VOBodyWithImage(cooldown.spanWithDotaImages(application, true), R.drawable.cooldown, application.getString(R.string.desc_cooldown))) }
//        mana?.let { mana -> add(VOBodyWithImage(mana.spanWithDotaImages(application, true), R.drawable.mana, application.getString(R.string.desc_mana))) }
//        params?.let { params ->
////            add(VOHeroInfoHeaderSound(application.getString(R.string.hero_fragment_params)))
//            add(VOBodyWithSeparator((params.toStringListWithDash()).spanWithDotaImages(application)))
//        }
//        story?.let { story ->
////            add(VOHeroInfoHeaderSound(application.getString(R.string.hero_fragment_story)))
//            add(VOBodyWithSeparator(SpannableString(story)))
//        }
//        notes?.let { notes ->
////            add(VOHeroInfoHeaderSound(application.getString(R.string.hero_fragment_notes)))
//            add(VOBodyWithSeparator(SpannableString(notes.toStringListWithDash())))
//        }
//    }
//)

