plugins {
    id("dev.detekt")
}

detekt {
    config.from(rootProject.file("detekt-config.yml"))
    buildUponDefaultConfig = false
    baseline = file("detekt-baseline.xml")
}
