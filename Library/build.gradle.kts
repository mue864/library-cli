plugins {
    id("java")
}

group = "org.library"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
//    apache http
    implementation("org.apache.httpcomponents.client5:httpclient5:5.1")
//    json
    implementation("org.json:json:20231013")
//    this is for sqlite
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
}

tasks.test {
    useJUnitPlatform()
}