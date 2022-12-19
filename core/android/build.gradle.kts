plugins {
    id("convention.android-library") // возможно стоит заменить на kotlinLibrary() и использовать зависимости compileOnly("com.google.android:android:4.1.1.4")
}

//android {
//    namespace = "n7.ad2.android"
//}

dependencies {
    implementation(libs.exif)
    api(libs.recyclerView)
    api(libs.paging3)
    api(libs.constraintLayout)
    api(libs.livedata)
    api(libs.viewmodel)
    api(libs.lifecycleRuntime)

    api(project(Module.Core.ui))
    api(project(Module.Core.ktx))
    api(project(Module.Core.dagger))
    api(project(Module.Core.common))

    implementation(libs.coil)
}