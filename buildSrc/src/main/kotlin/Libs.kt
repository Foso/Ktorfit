import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project

internal val Project.libs get() = project.extensions.getByName("libs") as LibrariesForLibs