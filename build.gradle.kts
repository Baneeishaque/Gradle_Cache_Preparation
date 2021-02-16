plugins {
    java
    application
}

group = "ndk.dk.cache"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(14))
    }
}

application {
    mainClass.set("Main")
}

dependencies {
    implementation("commons-io", "commons-io", "2.8.0")
}
