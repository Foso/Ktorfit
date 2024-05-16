package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit

public interface ClassProvider<T> {
    public fun create(_ktorfit: Ktorfit): T
}