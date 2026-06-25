package de.jensklingenberg.ktorfit

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "Usage it requires explicit opt-in via plugin config enableFactoryRegistry. This API is experimental for Ktorfit, it could be removed or changed without notice.",
)
public annotation class ExperimentalFactoryRegistry
