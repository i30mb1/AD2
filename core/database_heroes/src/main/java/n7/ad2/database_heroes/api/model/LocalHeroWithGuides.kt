package n7.ad2.database_heroes.api.model

import androidx.room.Embedded
import androidx.room.Relation
import n7.ad2.database_guides.model.LocalGuide

data class LocalHeroWithGuides(
    @Embedded val hero: LocalHero,
    @Relation(
        parentColumn = "name",
        entity = LocalGuide::class,
        entityColumn = "name"
    )
    val guides: List<LocalGuide>,
)