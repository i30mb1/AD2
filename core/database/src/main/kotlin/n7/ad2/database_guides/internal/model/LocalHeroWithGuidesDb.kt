package n7.ad2.database_guides.internal.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocalHeroWithGuidesDb(
    @Embedded val hero: HeroDb,
    @Relation(
        parentColumn = "name",
        entity = LocalGuide::class,
        entityColumn = "name"
    )
    val guides: List<LocalGuide>,
)
