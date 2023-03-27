pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

//rootProject.buildFileName = "shadowJar-build.gradle.kts"
rootProject.buildFileName = "build.gradle.kts"