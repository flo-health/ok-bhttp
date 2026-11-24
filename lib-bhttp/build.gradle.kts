import health.flo.network.gradle.PublishingConfig
import health.flo.network.gradle.setupJarPublishing
import health.flo.network.gradle.setupPublishingRepositories
import health.flo.network.gradle.setupTestsJava

apply {
    from("${rootProject.projectDir}/gradle/unit-testing.gradle.kts")
}

plugins {
    `java-library`
    kotlin("jvm")
    id("maven-publish")
}

dependencies {
    implementation(libs.network.okhttp)
}

setupTestsJava()

publishing {
    setupJarPublishing(
        publishing = this,
        config = PublishingConfig(
            artifactId = "ok-bhttp",
            version = "0.2.0",
        ),
    )
    setupPublishingRepositories(publishing = this)
}
