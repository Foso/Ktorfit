import org.gradle.plugins.signing.Sign

plugins {
    id("com.vanniktech.maven.publish")
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
}

tasks.withType<Sign>().configureEach {
    onlyIf("not publishing to mavenLocal") {
        !gradle.taskGraph.allTasks.any {
            it.name.contains("publishToMavenLocal", ignoreCase = true) ||
                    it.name.contains("publishAppleToMavenLocal", ignoreCase = true)
        }
    }
}
