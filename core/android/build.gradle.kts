plugins {
    androidLibrary()
}

dependencies {
    api(Lib.recyclerView)
    api(Lib.paging3)
    api(Lib.constraintLayout)
    api(project(Module.Core.ui))
    api(project(Module.Core.ktx))

    implementation(Lib.coil)
}