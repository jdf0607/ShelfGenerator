plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "edu.upc.prop.cluster.Main")) // Adjust according to your main class name
    }
}

application {
    mainClass.set("edu.upc.prop.cluster.Main")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "edu.upc.prop.cluster.Main"
        )
    }
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:4.8.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")

}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Xmx12g")
}


tasks.withType<JavaExec> {
    standardInput = System.`in`

}

tasks.withType<Test> {
    enabled = false

}

tasks.javadoc {
    options.encoding = "UTF-8"
    (options as StandardJavadocDocletOptions).apply {
        charSet = "UTF-8"
        links("https://docs.oracle.com/en/java/javase/17/docs/api/")
    }
}