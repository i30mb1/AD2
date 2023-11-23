plugins {
    id("convention.android-library") // возможно стоит заменить на kotlinLibrary() и использовать зависимости compileOnly("com.google.android:android:4.1.1.4")
}

dependencies {
    implementation(libs.exif)
    api(libs.recyclerView)
    api(libs.paging3)
    api(libs.constraintLayout)
    api(libs.livedata)
    api(libs.viewmodel)
    api(libs.lifecycleRuntime)

    api(projects.core.ui)
    api(projects.core.ktx)
    api(projects.core.dagger)
    api(projects.core.commonJvm)

    implementation(libs.coil)
}
