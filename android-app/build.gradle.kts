buildscript {
    dependencies {
        constraints {
            libs.bundles.patched.build.tools.get().forEach { classpath("${it.module}:${it.version}") }
        }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.jvm) apply false
}
