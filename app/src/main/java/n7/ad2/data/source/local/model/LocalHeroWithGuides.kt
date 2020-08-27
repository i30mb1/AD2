package n7.ad2.data.source.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocalHeroWithGuides(
    @Embedded val hero: LocalHero,
    @Relation(
        parentColumn = "name",
        entity = LocalGuide::class,
        entityColumn = "name"
    )
    val guides: List<LocalGuide>
)