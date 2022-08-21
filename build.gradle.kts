plugins {
    id("com.android.application") version "7.1.3" apply false
    kotlin("android") version "1.6.10" apply false
    kotlin("kapt") version "1.7.10" apply false
    id("com.google.dagger.hilt.android") version "2.42" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.1" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.6.10" apply false
}

tasks.register<Delete>("clean") {
    group = "build"
    delete(rootProject.buildDir)
}

tasks.register<Copy>("installGitHooks") {
    description = "Install project git hooks in the .git/hooks folder"
    group = "install"

    from(File(rootProject.rootDir, "scripts/pre-commit"))
    into { File(rootProject.rootDir, ".git/hooks") }
}
