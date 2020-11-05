plugins {

    java
    application
}

group = "ndk.dk.cache"
version = "1.0-SNAPSHOT"

application {

    mainClass.set("Main")
}

repositories {

    mavenCentral()
}

dependencies {

    implementation("commons-io", "commons-io", "2.8.0")
}
