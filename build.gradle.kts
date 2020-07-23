plugins {

    java
    application
}

group = "ndk.dk.cache"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "Main"
}

repositories {

    mavenCentral()
}

dependencies {

    compile ("commons-io","commons-io","2.6")
//    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {

    sourceCompatibility = JavaVersion.VERSION_1_8
}
