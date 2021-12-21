plugins {
    androidLibrary() // возможно стоит заменить на kotlinLibrary() и использовать зависимости compileOnly("com.google.android:android:4.1.1.4")
}

dependencies {
    api(Lib.recyclerView)
    api(Lib.paging3)
    api(Lib.constraintLayout)
    api(Lib.livedata)
    api(Lib.viewmodel)
    api(Lib.lifecycleRuntime)

    api(project(Module.Core.ui))
    api(project(Module.Core.ktx))
    api(project(Module.Core.dagger))

    implementation(Lib.coil)
}