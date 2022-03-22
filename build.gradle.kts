plugins {
    java
}

group = "com.github.konicai"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.named("build") {
    dependsOn(tasks.named<Test>("test"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("net.kyori:adventure-api:4.10.1")
    testImplementation("net.kyori:adventure-text-minimessage:4.10.1")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.10.1")
}